

package com.kongji.onecoupon.engine.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kongji.onecoupon.engine.dao.entity.CouponTemplateDO;
import com.kongji.onecoupon.engine.dto.resp.CouponTemplateQueryRespDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface CouponTemplateMapper extends BaseMapper<CouponTemplateDO> {

    /**
     * 自减优惠券模板库存
     *
     * @param couponTemplateId 优惠券模板 ID
     * @return 是否发生记录变更
     */
    int decrementCouponTemplateStock(@Param("shopNumber") Long shopNumber, @Param("couponTemplateId") Long couponTemplateId, @Param("decrementStock") Long decrementStock);

    @Select("select * from t_coupon_template_15 limit 10")
    List<CouponTemplateQueryRespDTO> selectAll();
}
