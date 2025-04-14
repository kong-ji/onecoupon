package org.kongji.chataimodel.AI.message;


import org.kongji.chataimodel.common.constant.ChatMemoryRedisConstant;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/12
 */

/**
 *
 * 实现记忆接口自定义记忆类
 */
@Component
public class OneCouponChatMemory implements ChatMemory {

    private final RedisTemplate<String, Message> redisTemplate;

    public OneCouponChatMemory(RedisTemplate<String, Message> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = ChatMemoryRedisConstant.COUPON_AI_CONVERSATIONS_KEY + conversationId;
        // 将消息列表顺序存入 Redis List
        redisTemplate.opsForList().rightPushAll(key, messages);
        // 设置过期时间（例如 24 小时）
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        String key = ChatMemoryRedisConstant.COUPON_AI_CONVERSATIONS_KEY + conversationId;
        // 获取最新的 lastN 条消息（从尾部截取）
        long total = redisTemplate.opsForList().size(key);
        if (total == 0) return Collections.emptyList();

        int start = Math.max(0, (int) (total - lastN));
        return redisTemplate.opsForList().range(key, start, -1);
    }

    @Override
    public void clear(String conversationId) {
        String key = ChatMemoryRedisConstant.COUPON_AI_CONVERSATIONS_KEY + conversationId;
        redisTemplate.delete(key);
    }
}