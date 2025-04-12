

package com.kongji.onecoupon.distribution.mq.consumer;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kongji.onecoupon.distribution.common.enums.CouponTaskStatusEnum;
import com.kongji.onecoupon.distribution.common.enums.CouponTemplateStatusEnum;
import com.kongji.onecoupon.distribution.dao.entity.CouponTemplateDO;
import com.kongji.onecoupon.distribution.dao.mapper.CouponTaskFailMapper;
import com.kongji.onecoupon.distribution.dao.mapper.CouponTaskMapper;
import com.kongji.onecoupon.distribution.dao.mapper.CouponTemplateMapper;
import com.kongji.onecoupon.distribution.dao.mapper.UserCouponMapper;
import com.kongji.onecoupon.distribution.mq.base.MessageWrapper;
import com.kongji.onecoupon.distribution.mq.event.CouponTaskExecuteEvent;
import com.kongji.onecoupon.distribution.mq.producer.CouponExecuteDistributionProducer;
import com.kongji.onecoupon.distribution.service.handler.excel.CouponTaskExcelObject;
import com.kongji.onecoupon.distribution.service.handler.excel.ReadExcelDistributionListener;
import com.kongji.onecoupon.framework.idempotent.NoMQDuplicateConsume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "one-coupon_distribution-service_coupon-task-execute_topic${unique-name:}",
        consumerGroup = "one-coupon_distribution-service_coupon-task-execute_cg${unique-name:}"
)
@Slf4j(topic = "CouponTaskExecuteConsumer")
public class CouponTaskExecuteConsumer implements RocketMQListener<MessageWrapper<CouponTaskExecuteEvent>> {

    private final CouponTaskMapper couponTaskMapper;
    private final CouponTemplateMapper couponTemplateMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final CouponTaskFailMapper couponTaskFailMapper;
    private final CouponExecuteDistributionProducer couponExecuteDistributionProducer;

    @NoMQDuplicateConsume(
            keyPrefix = "coupon_task_execute:idempotent:",
            key = "#messageWrapper.message.couponTaskId",
            keyTimeout = 120
    )
    @Override
    public void onMessage(MessageWrapper<CouponTaskExecuteEvent> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 优惠券推送任务正式执行 - 执行消费逻辑，消息体：{}", JSON.toJSONString(messageWrapper));

        // 判断优惠券模板发送状态是否为执行中，如果不是有可能是被取消状态
        var couponTaskId = messageWrapper.getMessage().getCouponTaskId();
        var couponTaskDO = couponTaskMapper.selectById(couponTaskId);
        if (ObjectUtil.notEqual(couponTaskDO.getStatus(), CouponTaskStatusEnum.IN_PROGRESS.getStatus())) {
            log.warn("[消费者] 优惠券推送任务正式执行 - 推送任务记录状态异常：{}，已终止推送", couponTaskDO.getStatus());
            return;
        }

        // 判断优惠券状态是否正确
        var queryWrapper = Wrappers.lambdaQuery(CouponTemplateDO.class)
                .eq(CouponTemplateDO::getId, couponTaskDO.getCouponTemplateId())
                .eq(CouponTemplateDO::getShopNumber, couponTaskDO.getShopNumber());
        var couponTemplateDO = couponTemplateMapper.selectOne(queryWrapper);
        var status = couponTemplateDO.getStatus();
        if (ObjectUtil.notEqual(status, CouponTemplateStatusEnum.ACTIVE.getStatus())) {
            log.error("[消费者] 优惠券推送任务正式执行 - 优惠券ID：{}，优惠券模板状态：{}", couponTaskDO.getCouponTemplateId(), status);
            return;
        }

        // 正式开始执行优惠券推送任务
        var readExcelDistributionListener = new ReadExcelDistributionListener(
                couponTaskDO,
                couponTemplateDO,
                couponTaskFailMapper,
                stringRedisTemplate,
                couponExecuteDistributionProducer
        );
        EasyExcel.read(couponTaskDO.getFileAddress(), CouponTaskExcelObject.class, readExcelDistributionListener).sheet().doRead();
    }
}
