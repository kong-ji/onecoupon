  

package com.kongji.onecoupon.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kongji.onecoupon.engine.dao.entity.CouponTemplateDO;
import com.kongji.onecoupon.engine.dto.req.CouponTemplateQueryReqDTO;
import com.kongji.onecoupon.engine.dto.resp.CouponTemplateQueryRespDTO;

import java.util.List;

/**
 * 优惠券模板业务逻辑层
 * <p>
 * 作者：kongji
 *    
 *     
 */
public interface CouponTemplateService extends IService<CouponTemplateDO> {

    /**
     * 查询优惠券模板
     *
     * @param requestParam 请求参数
     * @return 优惠券模板信息
     */
    CouponTemplateQueryRespDTO findCouponTemplate(CouponTemplateQueryReqDTO requestParam);

    /**
     * 根据优惠券id集合查询出券信息
     *
     * @param couponTemplateIds 优惠券id集合
     */
    List<CouponTemplateDO> listCouponTemplateByIds(List<Long> couponTemplateIds, List<Long> shopNumbers);

    /**
     * 主页默认查询优惠券模板
     * @return 优惠券列表
     */
    List<CouponTemplateQueryRespDTO> findAllCouponTemplate();
}
