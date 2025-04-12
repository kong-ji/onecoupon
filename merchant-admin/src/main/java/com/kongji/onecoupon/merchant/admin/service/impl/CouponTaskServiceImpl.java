  

package com.kongji.onecoupon.merchant.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kongji.onecoupon.framework.exception.ClientException;
import com.kongji.onecoupon.merchant.admin.common.context.UserContext;
import com.kongji.onecoupon.merchant.admin.common.enums.CouponTaskSendTypeEnum;
import com.kongji.onecoupon.merchant.admin.common.enums.CouponTaskStatusEnum;
import com.kongji.onecoupon.merchant.admin.common.enums.CouponTemplateStatusEnum;
import com.kongji.onecoupon.merchant.admin.dao.entity.CouponTaskDO;
import com.kongji.onecoupon.merchant.admin.dao.entity.CouponTemplateDO;
import com.kongji.onecoupon.merchant.admin.dao.mapper.CouponTaskMapper;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTaskCreateReqDTO;
import com.kongji.onecoupon.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;
import com.kongji.onecoupon.merchant.admin.mq.event.CouponTaskExecuteEvent;
import com.kongji.onecoupon.merchant.admin.mq.producer.CouponTaskActualExecuteProducer;
import com.kongji.onecoupon.merchant.admin.mq.producer.CouponTemplateDelayExecuteStatusProducer;
import com.kongji.onecoupon.merchant.admin.service.CouponTaskService;
import com.kongji.onecoupon.merchant.admin.service.CouponTemplateService;
import com.kongji.onecoupon.merchant.admin.service.handler.excel.RowCountListener;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.*;


@Service
@RequiredArgsConstructor
public class CouponTaskServiceImpl extends ServiceImpl<CouponTaskMapper, CouponTaskDO> implements CouponTaskService {

    private final CouponTemplateService couponTemplateService;
    private final CouponTaskMapper couponTaskMapper;
    private final RedissonClient redissonClient;
    private final CouponTaskActualExecuteProducer couponTaskActualExecuteProducer;
    /**
     * 为什么这里拒绝策略使用直接丢弃任务？因为在发送任务时如果遇到发送数量为空，会重新进行统计
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(
            //核心线程数:  核心线程数设置为 当前设备可用的CPU核心数
            Runtime.getRuntime().availableProcessors(),
            //最大线程数: 最大线程数设置为核心线程数的 2 倍。
            Runtime.getRuntime().availableProcessors() << 1,
            //线程空闲时间: 非核心线程（大于核心线程数的部分）在 60 秒内空闲时会被回收
            60,
            //时间单位: 空闲时间的时间单位
            TimeUnit.SECONDS,
            // 任务队列:  此处是同步队列：不存储任务，直接交给线程执行 新任务必须直接交给线程执行
            new SynchronousQueue<>(),
            //使用默认的线程工厂

            //拒绝策略: 丢弃任务，不抛出异常。因为有兜底机制
            new ThreadPoolExecutor.DiscardPolicy()
    );
    private final RedissonClient redisson;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createCouponTask(CouponTaskCreateReqDTO requestParam) {
        // 验证非空参数
        // 验证参数是否正确，比如文件地址是否为我们期望的格式等
        // 验证参数依赖关系，比如选择定时发送，发送时间是否不为空等
        CouponTemplateQueryRespDTO couponTemplate = couponTemplateService.findCouponTemplateById(requestParam.getCouponTemplateId());
        if (couponTemplate == null) {
            throw new ClientException("优惠券模板不存在，请检查提交信息是否正确");
        }
        // ......

        // 构建优惠券推送任务数据库持久层实体
        CouponTaskDO couponTaskDO = BeanUtil.copyProperties(requestParam, CouponTaskDO.class);
        couponTaskDO.setBatchId(IdUtil.getSnowflakeNextId());
        couponTaskDO.setOperatorId(Long.parseLong(UserContext.getUserId()));
        couponTaskDO.setShopNumber(UserContext.getShopNumber());
        couponTaskDO.setStatus(
                Objects.equals(requestParam.getSendType(), CouponTaskSendTypeEnum.IMMEDIATE.getType())
                        ? CouponTaskStatusEnum.IN_PROGRESS.getStatus()
                        : CouponTaskStatusEnum.PENDING.getStatus()
        );

        // 保存优惠券推送任务记录到数据库
        couponTaskMapper.insert(couponTaskDO);

        //提交任务到线程池
        //封装任务参数
        JSONObject delayJsonObject = JSONObject
                .of("fileAddress",requestParam.getFileAddress(),"couponTaskId",couponTaskDO.getId());
        executorService.execute(()->refreshCouponTaskSendNum(delayJsonObject));

        //redis延迟队列实现兜底机制
        //获取阻塞队列 支持任务消费，并能通过阻塞方式高效等待任务
        RBlockingDeque<Object>blockingDeque=redissonClient.getBlockingDeque("COUPON_TASK_SEND_NUM_DELAY_QUEUE");
        //获取延迟队列
        RDelayedQueue<Object>delayedQueue=redissonClient.getDelayedQueue(blockingDeque);
        //设置延迟时间20s
        delayedQueue.offer(delayJsonObject,20,TimeUnit.SECONDS);
        //到目前为止，只是把发送任务写入数据库和解析excel数据条数 完成

        //判断是否是立刻批量发送发送  这里只是发送到mq，后面还需要分发服务订阅对应的topic对消息处理后进行优惠券批量分发
        if(Objects.equals(requestParam.getSendType(), CouponTaskSendTypeEnum.IMMEDIATE.getType())){
            // 执行优惠券推送业务，正式向用户发放优惠券
            CouponTaskExecuteEvent couponTaskExecuteEvent = CouponTaskExecuteEvent.builder()
                    .couponTaskId(couponTaskDO.getId())
                    .build();
            couponTaskActualExecuteProducer.sendMessage(couponTaskExecuteEvent);
        }
    }
    private void refreshCouponTaskSendNum(JSONObject delayJsonObject) {
        // 通过 EasyExcel 监听器获取 Excel 中所有行数
        RowCountListener listener = new RowCountListener();
        EasyExcel.read(delayJsonObject.getString("fileAddress"), listener).sheet().doRead();

        // 为什么需要统计行数？因为发送后需要比对所有优惠券是否都已发放到用户账号
        int totalRows = listener.getRowCount();
        CouponTaskDO updateCouponTaskDO = CouponTaskDO.builder()
                                .id(delayJsonObject.getLong("couponTaskId"))
                                .sendNum(totalRows)
                                .build();
        couponTaskMapper.updateById(updateCouponTaskDO);
    }

    @Service
    @RequiredArgsConstructor
    class RefreshCouponTaskDelayQueueRunner implements CommandLineRunner {
        private final CouponTaskMapper couponTaskMapper;
        private final RedissonClient redissonClient;

        @Override
        public void run(String... args) throws Exception {
            Executors.newSingleThreadExecutor(
                    runnable->{
                        Thread thread = new Thread(runnable);
                        thread.setName("delay_coupon-task_send-num_consumer");
                        thread.setDaemon(Boolean.TRUE);
                        return thread;
                    }
            ).execute(()->{
                RBlockingDeque<JSONObject>blockingDeque=redissonClient.getBlockingDeque("COUPON_TASK_SEND_NUM_DELAY_QUEUE");
                while(true){
                    try{
                        //获取延迟队列已经到达时间的任务
                        JSONObject delayJsonObject=blockingDeque.take();
                        //判断是否有任务到期
                        if(delayJsonObject!=null){
                            CouponTaskDO couponTaskDO=couponTaskMapper.selectById(delayJsonObject.getLong("couponTaskId"));
                            //判断是否需要执行兜底更新
                            if(couponTaskDO.getSendNum()==null){
                                refreshCouponTaskSendNum(delayJsonObject);
                            }
                        }
                    }catch (Throwable ignore){

                    }
                }
            });
        }
    }

}

