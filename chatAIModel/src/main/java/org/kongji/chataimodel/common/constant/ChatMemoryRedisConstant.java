package org.kongji.chataimodel.common.constant;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/12
 */

/**
 * Redis 记忆聊天常量类
 */
public final class ChatMemoryRedisConstant {
    /**
     * AI记忆缓存 Key
     */
    public static final String COUPON_AI_CONVERSATIONS_KEY = "one-coupon_ai:user-chat-memory:%s";
    /**
     * 游戏聊天缓存 Key
     */
    public static final String COUPON_AI_GAME_KEY = "one-coupon_ai:user-game-memory:%s";
    /**
     * 服务缓存 Key
     */
    public static final String COUPON_AI_SERVICE_KEY = "one-coupon_ai:user-service-memory:%s";
    /**
     * 错误 Key
     */
    public static final String COUPON_AI_ERROR_KEY = "one-coupon_ai:error:%s";
    /**
     * AI聊天历史 业务类型 Key
     */
    public static final String COUPON_AI_HISTORY_MESSAGE_CHAT_TYPE_IDS_KEY = "one-coupon_ai:user-chat-history-type-ids:%s";
}
