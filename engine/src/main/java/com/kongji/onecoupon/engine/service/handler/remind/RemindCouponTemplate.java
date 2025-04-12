  

package com.kongji.onecoupon.engine.service.handler.remind;

import com.kongji.onecoupon.engine.service.handler.remind.dto.CouponTemplateRemindDTO;

/**
 * 优惠券抢券提醒接口
 * <p>
 * 作者：    kongji
 *    
 *     
 */
public interface RemindCouponTemplate {

    /**
     * 提醒用户抢券
     *
     * @param remindCouponTemplateDTO 提醒所需要的信息
     */
    boolean remind(CouponTemplateRemindDTO remindCouponTemplateDTO);
}
