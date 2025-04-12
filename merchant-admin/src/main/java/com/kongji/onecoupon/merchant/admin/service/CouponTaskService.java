  

package com.kongji.onecoupon.merchant.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kongji.onecoupon.merchant.admin.dao.entity.CouponTaskDO;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTaskCreateReqDTO;

/**
 * 优惠券推送业务逻辑层
 * <p>
 * 作者：kongji
 *    
 *     
 */
public interface CouponTaskService extends IService<CouponTaskDO> {

    /**
     * 商家创建优惠券推送任务
     *
     * @param requestParam 请求参数
     */
    void createCouponTask(CouponTaskCreateReqDTO requestParam);
}
