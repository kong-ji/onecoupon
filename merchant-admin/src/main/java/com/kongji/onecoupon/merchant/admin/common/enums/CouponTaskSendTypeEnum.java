  

package com.kongji.onecoupon.merchant.admin.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券推送任务发送类型枚举
 * <p>
 * 作者：kongji
 *    
 *     
 */
@RequiredArgsConstructor
public enum CouponTaskSendTypeEnum {

    /**
     * 立即发送
     */
    IMMEDIATE(0),

    /**
     * 定时发送
     */
    SCHEDULED(1);

    @Getter
    private final int type;
}
