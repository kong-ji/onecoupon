  

package com.kongji.onecoupon.distribution.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券来源枚举类
 * <p>
 * 作者：kongji
 *
 */
@RequiredArgsConstructor
public enum CouponSourceEnum {

    /**
     * 店铺券
     */
    SHOP(0),

    /**
     * 平台券
     */
    PLATFORM(1);

    @Getter
    private final int type;
}
