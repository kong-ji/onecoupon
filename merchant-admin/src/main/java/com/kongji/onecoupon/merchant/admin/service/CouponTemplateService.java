

package com.kongji.onecoupon.merchant.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kongji.onecoupon.merchant.admin.dao.entity.CouponTemplateDO;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTemplateNumberReqDTO;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTemplatePageQueryReqDTO;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import com.kongji.onecoupon.merchant.admin.dto.resp.CouponTemplatePageQueryRespDTO;
import com.kongji.onecoupon.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;

/**
 * 优惠券模板业务逻辑层
 * <p>
 * 作者：kongji
 *
 *
 */
public interface CouponTemplateService extends IService<CouponTemplateDO> {

    /**
     * 创建商家优惠券模板
     *
     * @param requestParam 请求参数
     */
    void createCouponTemplate(CouponTemplateSaveReqDTO requestParam);

    /**
     * 分页查询优惠券模板
     * @param requestParam 查询参数
     * @return 查询结果
     */
    IPage<CouponTemplatePageQueryRespDTO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam);

    /**
     * 查询优惠券详细信息
     * @param couponTemplateId 优惠券Id
     * @return 查询结果
     */
    CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId);

    /**
     * 增加发行量
     * @param requestParam 参数
     */
    void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam);

    /**
     * 终止优惠券
     * @param couponTemplateId 参数
     */
    void terminateCouponTemplate(String couponTemplateId);
}
