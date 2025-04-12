

package com.kongji.onecoupon.merchant.admin.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券推送任务状态枚举
 * <p>
 * 作者：kongji
 *    
 *     
 */
@RequiredArgsConstructor
public enum CouponTaskStatusEnum {

    /**
     * 待执行
     */
    PENDING(0),

    /**
     * 执行中
     */
    IN_PROGRESS(1),

    /**
     * 执行失败
     */
    FAILED(2),

    /**
     * 执行成功
     */
    SUCCESS(3),

    /**
     * 取消
     */
    CANAL(4);

    @Getter
    private final int status;
}
