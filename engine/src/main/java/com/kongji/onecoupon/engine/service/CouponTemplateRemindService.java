

package com.kongji.onecoupon.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kongji.onecoupon.engine.dao.entity.CouponTemplateRemindDO;
import com.kongji.onecoupon.engine.dto.req.CouponTemplateRemindCancelReqDTO;
import com.kongji.onecoupon.engine.dto.req.CouponTemplateRemindCreateReqDTO;
import com.kongji.onecoupon.engine.dto.req.CouponTemplateRemindQueryReqDTO;
import com.kongji.onecoupon.engine.dto.resp.CouponTemplateRemindQueryRespDTO;
import com.kongji.onecoupon.engine.service.handler.remind.dto.CouponTemplateRemindDTO;

import java.util.List;

/**
 * 优惠券预约提醒业务逻辑层
 * <p>
 * 作者：    kongji
 *    
 *     
 */
public interface CouponTemplateRemindService extends IService<CouponTemplateRemindDO> {

    /**
     * 创建抢券预约提醒
     *
     * @param requestParam 请求参数
     */
    void createCouponRemind(CouponTemplateRemindCreateReqDTO requestParam);

    /**
     * 分页查询抢券预约提醒
     *
     * @param requestParam 请求参数
     */
    List<CouponTemplateRemindQueryRespDTO> listCouponRemind(CouponTemplateRemindQueryReqDTO requestParam);

    /**
     * 取消抢券预约提醒
     *
     * @param requestParam 请求参数
     */
    void cancelCouponRemind(CouponTemplateRemindCancelReqDTO requestParam);

    /**
     * 检查是否取消抢券预约提醒
     *
     * @param requestParam 请求参数
     */
    boolean isCancelRemind(CouponTemplateRemindDTO requestParam);
}
