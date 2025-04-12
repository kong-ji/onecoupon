

package com.kongji.onecoupon.engine;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Date;

/**
 * RocketMQ5.x 延迟生产消费者单元测试
 * <p>
 * 作者：    kongji
 *    
 *     
 */
@Slf4j
@SpringBootTest
public final class RocketMQ5xDelayProducerConsumerTests {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @SneakyThrows
    @Test
    public void producerSendTest() {
        DateTime remindTime = DateUtil.offsetSecond(new Date(), 5);
        // 创建消息
        Message message = new Message("TestDelayTopic", "aaa".getBytes());
        message.setDeliverTimeMs(remindTime.getTime());
        DefaultMQProducer defaultMQProducer = rocketMQTemplate.getProducer();
        SendResult sendResult = defaultMQProducer.send(message);
        log.info("延迟消息队列发送结果：{}，当前发送时间：{}", sendResult, DateUtil.formatTime(new Date()));
        Thread.sleep(10000);
    }

    @TestConfiguration
    static class RocketMQ5xConfiguration {
        @Bean
        public RocketMQ5xConsumerTests rocketMQ5xConsumerTests() {
            return new RocketMQ5xConsumerTests();
        }
    }

    @RocketMQMessageListener(
            topic = "TestDelayTopic",
            consumerGroup = "TestDelayTopic_CG"
    )
    static class RocketMQ5xConsumerTests implements RocketMQListener<String> {

        @Override
        public void onMessage(String message) {
            log.info("接收到消费消息：{}，当前接收时间：{}", message, DateUtil.formatTime(new Date()));
        }
    }
}


