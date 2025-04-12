  

package com.kongji.onecoupon.merchant.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kongji.onecoupon.merchant.admin.dao.entity.CouponTemplateDO;
import org.apache.ibatis.annotations.Param;

/**
 * 优惠券模板数据库持久层
 * <p>
 * 作者：kongji
 *    
 *
 */
public interface CouponTemplateMapper extends BaseMapper<CouponTemplateDO> {
    /**
     * 增加优惠券模板发行量
     * @param shopNumber 店铺编号
     * @param couponTemplateId 优惠券Id
     * @param number 增加数量
     */
    int increaseNumberCouPonTemplate(@Param("number") Integer number,@Param("shopNumber") Long shopNumber,@Param("couponTemplateId") String couponTemplateId);
}
