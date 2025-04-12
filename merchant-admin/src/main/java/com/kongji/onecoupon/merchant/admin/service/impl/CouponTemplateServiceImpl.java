

package com.kongji.onecoupon.merchant.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import com.kongji.onecoupon.framework.exception.ClientException;
import com.kongji.onecoupon.framework.exception.ServiceException;
import com.kongji.onecoupon.merchant.admin.common.constant.MerchantAdminRedisConstant;
import com.kongji.onecoupon.merchant.admin.common.context.UserContext;
import com.kongji.onecoupon.merchant.admin.common.enums.CouponTemplateStatusEnum;
import com.kongji.onecoupon.merchant.admin.dao.entity.CouponTemplateDO;
import com.kongji.onecoupon.merchant.admin.dao.mapper.CouponTemplateMapper;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTemplateNumberReqDTO;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTemplatePageQueryReqDTO;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import com.kongji.onecoupon.merchant.admin.dto.resp.CouponTemplatePageQueryRespDTO;
import com.kongji.onecoupon.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;
import com.kongji.onecoupon.merchant.admin.mq.event.CouponTaskExecuteEvent;
import com.kongji.onecoupon.merchant.admin.mq.event.CouponTemplateDelayEvent;
import com.kongji.onecoupon.merchant.admin.mq.producer.CouponTemplateDelayExecuteStatusProducer;
import com.kongji.onecoupon.merchant.admin.service.CouponTemplateService;
import com.kongji.onecoupon.merchant.admin.service.basics.chain.MerchantAdminChainContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RBloomFilter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import static com.kongji.onecoupon.merchant.admin.common.enums.ChainBizMarkEnum.MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY;

/**
 * 优惠券模板业务逻辑实现层
 * <p>
 * 作者：kongji
 *    
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplateDO> implements CouponTemplateService {
    private final StringRedisTemplate redisTemplate;
    private final CouponTemplateMapper couponTemplateMapper;
    private final MerchantAdminChainContext merchantAdminChainContext;
    private final CouponTemplateDelayExecuteStatusProducer couponTemplateDelayExecuteStatusProducer;
    private final RBloomFilter<String>couponTemplateBloomFilter;
    /*
    success：方法执行成功后的日志模版。
    type：操作日志的类型，比如：订单类型、商品类型。
    bizNo：日志绑定的业务标识，需要是我们优惠券模板的 ID，但是目前拿不到，放一个占位符。
    extra：日志的额外信息。
     */
    @LogRecord(
            success = """
                创建优惠券：{{#requestParam.name}}， \
                优惠对象：{COMMON_ENUM_PARSE{'DiscountTargetEnum' + '_' + #requestParam.target}}， \
                优惠类型：{COMMON_ENUM_PARSE{'DiscountTypeEnum' + '_' + #requestParam.type}}， \
                库存数量：{{#requestParam.stock}}， \
                优惠商品编码：{{#requestParam.goods}}， \
                有效期开始时间：{{#requestParam.validStartTime}}， \
                有效期结束时间：{{#requestParam.validEndTime}}， \
                领取规则：{{#requestParam.receiveRule}}， \
                消耗规则：{{#requestParam.consumeRule}};
                """,
            type = "CouponTemplate",
            bizNo = "{{#bizNo}}",
            extra = "{{#requestParam.toString()}}")
    @Override
    public void createCouponTemplate(CouponTemplateSaveReqDTO requestParam) {
        //通过责任链判断参数是否合法
        merchantAdminChainContext.handler(MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY.name(),requestParam);

        //新增优惠券到数据库
        CouponTemplateDO couponTemplateDO = BeanUtil.copyProperties(requestParam, CouponTemplateDO.class);
        couponTemplateDO.setShopNumber(UserContext.getShopNumber());
        couponTemplateDO.setStatus(CouponTemplateStatusEnum.ACTIVE.getStatus());
        couponTemplateMapper.insert(couponTemplateDO);

        // 因为模板 ID 是运行中生成的，@LogRecord 默认拿不到，所以我们需要手动设置
        LogRecordContext.putVariable("bizNo", couponTemplateDO.getId());

        //缓存预热
        //转换成redis存储json串
        //先转成DTO
        CouponTemplateQueryRespDTO queryRespDTO = BeanUtil.copyProperties(couponTemplateDO, CouponTemplateQueryRespDTO.class);
        //将类对象映射成map
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(queryRespDTO);
        //再把字段转成string
        Map<String, String> actualCacheTargetMap = stringObjectMap.entrySet().stream().
                collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() != null ? entry.getValue().toString() : "")
                );
        //获取优惠券的key
        String Key = String.format(MerchantAdminRedisConstant.COUPON_TEMPLATE_KEY, couponTemplateDO.getId());

        // 通过 LUA 脚本执行设置 Hash 数据以及设置过期时间
        String luaScript = "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV - 1)) " +
                "redis.call('EXPIREAT', KEYS[1], ARGV[#ARGV])";
        List<String> keys = Collections.singletonList(Key);
        List<String> args = new ArrayList<>(actualCacheTargetMap.size() * 2 + 1);
        actualCacheTargetMap.forEach((key, value) -> {
            args.add(key);
            args.add(value);
        });
        // 优惠券活动过期时间转换为秒级别的 Unix 时间戳
        args.add(String.valueOf(couponTemplateDO.getValidEndTime().getTime() / 1000));

        // 执行 LUA 脚本
        redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, Long.class),
                keys,
                args.toArray()
        );



        //构建消息事件
        CouponTemplateDelayEvent templateDelayEvent=CouponTemplateDelayEvent.builder()
                .delayTime(couponTemplateDO.getValidEndTime().getTime())
                .couponTemplateId(couponTemplateDO.getId())
                .shopNumber(couponTemplateDO.getShopNumber())
                .build();

        couponTemplateDelayExecuteStatusProducer.sendMessage(templateDelayEvent);

        //将优惠券标识存入布隆过滤器
        couponTemplateBloomFilter.add(String.valueOf(couponTemplateDO.getId()));
    }

    @Override
    public IPage<CouponTemplatePageQueryRespDTO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam) {
        //构建查询条件构造器
        LambdaQueryWrapper<CouponTemplateDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateDO.class)
                .eq(CouponTemplateDO::getShopNumber,UserContext.getShopNumber())
                .like(StrUtil.isNotBlank(requestParam.getName()),CouponTemplateDO::getName,requestParam.getName())
                .like(StrUtil.isNotBlank(requestParam.getGoods()), CouponTemplateDO::getGoods, requestParam.getGoods())
                .eq(Objects.nonNull(requestParam.getType()), CouponTemplateDO::getType, requestParam.getType())
                .eq(Objects.nonNull(requestParam.getTarget()), CouponTemplateDO::getTarget, requestParam.getTarget());
        //执行分页插件
        CouponTemplatePageQueryReqDTO selectPage = baseMapper.selectPage(requestParam, queryWrapper);

        //转换数据返回
        return selectPage.convert(each -> BeanUtil.copyProperties(each, CouponTemplatePageQueryRespDTO.class));
    }

    @Override
    public CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId) {
        //构造查询器
        LambdaQueryWrapper<CouponTemplateDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateDO.class)
                .eq(CouponTemplateDO::getId, couponTemplateId)
                .eq(CouponTemplateDO::getShopNumber,UserContext.getShopNumber());
        CouponTemplateDO couponTemplateDO = baseMapper.selectOne(queryWrapper);
        return BeanUtil.copyProperties(couponTemplateDO, CouponTemplateQueryRespDTO.class);
    }

    @LogRecord(
            success = "增加发行量：{{#requestParam.number}}",
            type = "CouponTemplate",
            bizNo = "{{#requestParam.couponTemplateId}}"
    )
    @Override
    public void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam) {
        //验证是否存在横向越权
        LambdaQueryWrapper<CouponTemplateDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateDO.class)
                .eq(CouponTemplateDO::getId, requestParam.getCouponTemplateId())
                .eq(CouponTemplateDO::getShopNumber,UserContext.getShopNumber());
        CouponTemplateDO couponTemplateDO = baseMapper.selectOne(queryWrapper);
        if(couponTemplateDO == null) {
            throw  new ClientException("优惠券模板异常，请检查操作是否正确...");
        }
        // 验证优惠券模板是否正常
        if (ObjectUtil.notEqual(couponTemplateDO.getStatus(), CouponTemplateStatusEnum.ACTIVE.getStatus())) {
            throw new ClientException("优惠券模板已结束");
        }
        //写入之前的数据到日志
        LogRecordContext.putVariable("originalData", JSON.toJSONString(couponTemplateDO));
        //设置数据库增加发行量
        int increase=baseMapper.increaseNumberCouPonTemplate(requestParam.getNumber(),UserContext.getShopNumber(),requestParam.getCouponTemplateId());
        //判断是否增加成功
        if(increase!=1) {
            throw new ServiceException("优惠券模板增加发行失败");
        }
        // 增加redis中优惠券模板缓存库存发行量
        String couponTemplateCacheKey = String.format(MerchantAdminRedisConstant.COUPON_TEMPLATE_KEY, requestParam.getCouponTemplateId());
        redisTemplate.opsForHash().increment(couponTemplateCacheKey, "stock", requestParam.getNumber());
    }
    @LogRecord(
            success = "结束优惠券",
            type = "CouponTemplate",
            bizNo = "{{#couponTemplateId}}"
    )
    @Override
    public void terminateCouponTemplate(String couponTemplateId) {
        //TODO 基于责任链实现一条链中的处理器自由选择,而不是处理器全部执行,增加灵活性和扩展性

        // 验证是否存在数据横向越权
        LambdaQueryWrapper<CouponTemplateDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateDO.class)
                .eq(CouponTemplateDO::getShopNumber, UserContext.getShopNumber())
                .eq(CouponTemplateDO::getId, Long.valueOf(couponTemplateId));
        CouponTemplateDO couponTemplateDO = couponTemplateMapper.selectOne(queryWrapper);
        if (couponTemplateDO == null) {
            // 一旦查询优惠券不存在，基本可判定横向越权，可上报该异常行为，次数多了后执行封号等处理
            throw new ClientException("优惠券模板异常，请检查操作是否正确...");
        }

        // 验证优惠券模板是否正常
        if (ObjectUtil.notEqual(couponTemplateDO.getStatus(), CouponTemplateStatusEnum.ACTIVE.getStatus())) {
            throw new ClientException("优惠券模板已结束");
        }
        // 记录优惠券模板修改前数据
        LogRecordContext.putVariable("originalData", JSON.toJSONString(couponTemplateDO));
        // 修改优惠券模板为结束状态
        CouponTemplateDO updateCouponTemplateDO = CouponTemplateDO.builder()
                .status(CouponTemplateStatusEnum.ENDED.getStatus())
                .build();
        //构造更新器
        Wrapper<CouponTemplateDO> updateWrapper = Wrappers.lambdaUpdate(CouponTemplateDO.class)
                .eq(CouponTemplateDO::getId, couponTemplateDO.getId())
                .eq(CouponTemplateDO::getShopNumber, UserContext.getShopNumber());
        int update = baseMapper.update(updateCouponTemplateDO, updateWrapper);
        if(update != 1) {
            throw new ServiceException("结束优惠券失败,请联系管理员");
        }
        //更新redis的优惠券信息
        // 修改优惠券模板缓存状态为结束状态
        String couponTemplateCacheKey = String.format(MerchantAdminRedisConstant.COUPON_TEMPLATE_KEY, couponTemplateId);
        redisTemplate.opsForHash().put(couponTemplateCacheKey, "status", String.valueOf(CouponTemplateStatusEnum.ENDED.getStatus()));


    }
}