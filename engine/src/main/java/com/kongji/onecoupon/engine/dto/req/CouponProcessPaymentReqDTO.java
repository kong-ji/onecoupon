  

package com.kongji.onecoupon.engine.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 处理优惠券结算单请求参数实体
 * <p>
 * 作者：kongji
 *
 *
 */
@Data
public class CouponProcessPaymentReqDTO {

    /**
     * 优惠券ID
     */
    @Schema(description = "优惠券ID", required = true)
    private Long couponId;
}
