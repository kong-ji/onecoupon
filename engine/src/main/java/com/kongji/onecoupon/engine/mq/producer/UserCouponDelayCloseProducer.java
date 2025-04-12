  

package com.kongji.onecoupon.engine.mq.producer;

import cn.hutool.core.util.StrUtil;
import com.kongji.onecoupon.engine.common.constant.EngineRockerMQConstant;
import com.kongji.onecoupon.engine.mq.base.BaseSendExtendDTO;
import com.kongji.onecoupon.engine.mq.base.MessageWrapper;
import com.kongji.onecoupon.engine.mq.event.UserCouponDelayCloseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 用户优惠券延时关闭生产者
 * <p>
 * 作者：kongji
 *    
 *     
 */
@Slf4j
@Component
public class UserCouponDelayCloseProducer extends AbstractCommonSendProduceTemplate<UserCouponDelayCloseEvent> {

    private final ConfigurableEnvironment environment;

    public UserCouponDelayCloseProducer(@Autowired RocketMQTemplate rocketMQTemplate, @Autowired ConfigurableEnvironment environment) {
        super(rocketMQTemplate);
        this.environment = environment;
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(UserCouponDelayCloseEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("延迟关闭用户已领取优惠券")
                .keys(String.valueOf(messageSendEvent.getUserCouponId()))
                .topic(environment.resolvePlaceholders(EngineRockerMQConstant.USER_COUPON_DELAY_CLOSE_TOPIC_KEY))
                .sentTimeout(2000L)
                .delayTime(messageSendEvent.getDelayTime())
                .build();
    }

    @Override
    protected Message<?> buildMessage(UserCouponDelayCloseEvent messageSendEvent, BaseSendExtendDTO requestParam) {
        String keys = StrUtil.isEmpty(requestParam.getKeys()) ? UUID.randomUUID().toString() : requestParam.getKeys();
        return MessageBuilder
                .withPayload(new MessageWrapper(keys, messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();
    }
}
