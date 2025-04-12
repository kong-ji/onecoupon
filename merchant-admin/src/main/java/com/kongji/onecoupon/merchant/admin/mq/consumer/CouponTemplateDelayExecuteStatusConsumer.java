  

package com.kongji.onecoupon.merchant.admin.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kongji.onecoupon.merchant.admin.common.enums.CouponTemplateStatusEnum;
import com.kongji.onecoupon.merchant.admin.dao.entity.CouponTemplateDO;
import com.kongji.onecoupon.merchant.admin.mq.base.MessageWrapper;
import com.kongji.onecoupon.merchant.admin.mq.event.CouponTaskExecuteEvent;
import com.kongji.onecoupon.merchant.admin.mq.event.CouponTemplateDelayEvent;
import com.kongji.onecoupon.merchant.admin.service.CouponTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 优惠券推送延迟执行-变更记录发送状态消费者
 * <p>
 * 作者：kongji
 *    
 *
 */
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "one-coupon_merchant-admin-service_coupon-template-delay_topic${unique-name:}",
        consumerGroup = "one-coupon_merchant-admin-service_coupon-template-delay-status_cg${unique-name:}"
)
@Slf4j(topic = "CouponTemplateDelayExecuteStatusConsumer")
public class CouponTemplateDelayExecuteStatusConsumer implements RocketMQListener<MessageWrapper<CouponTemplateDelayEvent>> {

    private final CouponTemplateService couponTemplateService;

    @Override
    public void onMessage(MessageWrapper<CouponTemplateDelayEvent> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 优惠券模板定时执行@变更模板表状态 - 执行消费逻辑，消息体：{}", JSON.toJSONString(messageWrapper));

        // 修改指定优惠券模板状态为已结束
        CouponTemplateDelayEvent message = messageWrapper.getMessage();
        LambdaUpdateWrapper<CouponTemplateDO> updateWrapper = Wrappers.lambdaUpdate(CouponTemplateDO.class)
                .eq(CouponTemplateDO::getShopNumber, message.getShopNumber())
                .eq(CouponTemplateDO::getId, message.getCouponTemplateId())
                .set(CouponTemplateDO::getStatus, CouponTemplateStatusEnum.ENDED.getStatus());
        couponTemplateService.update(updateWrapper);
    }
}
