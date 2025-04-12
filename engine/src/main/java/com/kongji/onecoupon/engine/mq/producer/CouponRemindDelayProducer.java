  

package com.kongji.onecoupon.engine.mq.producer;

import cn.hutool.core.util.StrUtil;
import com.kongji.onecoupon.engine.mq.base.BaseSendExtendDTO;
import com.kongji.onecoupon.engine.mq.base.MessageWrapper;
import com.kongji.onecoupon.engine.mq.event.CouponRemindDelayEvent;
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
 * 提醒抢券生产者
 * <p>
 * 作者：    kongji
 *    
 *     
 */
@Slf4j
@Component
public class CouponRemindDelayProducer extends AbstractCommonSendProduceTemplate<CouponRemindDelayEvent> {

    private final ConfigurableEnvironment environment;

    public CouponRemindDelayProducer(@Autowired RocketMQTemplate rocketMQTemplate, @Autowired ConfigurableEnvironment environment) {
        super(rocketMQTemplate);
        this.environment = environment;
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(CouponRemindDelayEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("提醒用户抢券")
                .keys(messageSendEvent.getUserId() + ":" + messageSendEvent.getCouponTemplateId())
                .topic(environment.resolvePlaceholders("one-coupon_engine-service_coupon-remind_topic${unique-name:}"))
                .sentTimeout(2000L)
                .delayTime(messageSendEvent.getDelayTime())
                .build();
    }

    @Override
    protected Message<?> buildMessage(CouponRemindDelayEvent messageSendEvent, BaseSendExtendDTO requestParam) {
        String keys = StrUtil.isEmpty(requestParam.getKeys()) ? UUID.randomUUID().toString() : requestParam.getKeys();
        return MessageBuilder
                .withPayload(new MessageWrapper(keys, messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();
    }
}
