package org.kongji.chataimodel.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.kongji.chataimodel.common.constant.ChatMemoryRedisConstant;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
@RequiredArgsConstructor
public enum OneCouponAIHistoryKeyEnum {
    CHAT(0, ChatMemoryRedisConstant.COUPON_AI_CONVERSATIONS_KEY),
    GAME(1, ChatMemoryRedisConstant.COUPON_AI_GAME_KEY),
    SERVICE(2, ChatMemoryRedisConstant.COUPON_AI_SERVICE_KEY),
    ERROR(-1, ChatMemoryRedisConstant.COUPON_AI_ERROR_KEY);
    @Getter
    private final int type;
    @Getter
    private final String key;

    public static String getByType(Integer type){
        for(OneCouponAIHistoryKeyEnum oneCouponAITypeEnum : values()){
            if (oneCouponAITypeEnum.getType() == type) {
                return oneCouponAITypeEnum.getKey();
            }
        }
        return ERROR.getKey();
    }
}
