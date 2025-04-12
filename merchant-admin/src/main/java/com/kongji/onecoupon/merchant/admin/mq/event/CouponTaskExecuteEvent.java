

package com.kongji.onecoupon.merchant.admin.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券推送任务执行事件
 * <p>
 * 作者：kongji
 *    
 *     
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTaskExecuteEvent {

    /**
     * 推送任务id
     */
    private Long couponTaskId;
}
