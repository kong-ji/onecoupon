package org.kongji.chataimodel.AI.message;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.kongji.chataimodel.common.constant.ChatMemoryRedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
@Component
@RequiredArgsConstructor
public class OneCouponChatHistoryRepository implements ChatHistoryRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String type, String chatId) {
        String key = String.format(ChatMemoryRedisConstant.COUPON_AI_HISTORY_MESSAGE_CHAT_TYPE_IDS_KEY, type);
        redisTemplate.opsForSet().add(key,chatId);
    }

    @Override
    public List<String> getChatIds(String type) {
        String key = String.format(ChatMemoryRedisConstant.COUPON_AI_HISTORY_MESSAGE_CHAT_TYPE_IDS_KEY, type);
        // 获取Set后直接转换为List
        Set<String> members = redisTemplate.opsForSet().members(key);
        return new ArrayList<>(members != null ? members : Collections.emptySet());
    }
}
