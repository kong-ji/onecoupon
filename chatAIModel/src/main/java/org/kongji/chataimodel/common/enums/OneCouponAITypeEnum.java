package org.kongji.chataimodel.common.enums;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AI 模块业务类型枚举类
 */
@RequiredArgsConstructor
public enum OneCouponAITypeEnum {

    CHAT("chat"),
    SERVICE("service");


    @Getter
    private final String type;
}
