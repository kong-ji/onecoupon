package com.kongji.onecoupon.merchant.admin.service.handler.filter;

import cn.hutool.core.util.ObjectUtil;
import com.kongji.onecoupon.merchant.admin.common.enums.DiscountTargetEnum;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import com.kongji.onecoupon.merchant.admin.service.basics.chain.MerchantAdminAbstractChainHandler;
import org.springframework.stereotype.Component;

import static com.kongji.onecoupon.merchant.admin.common.enums.ChainBizMarkEnum.MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY;
@Component
public class CouponTemplateCreateParamVerifyChainFilter implements MerchantAdminAbstractChainHandler<CouponTemplateSaveReqDTO>{

    @Override
    public void handler(CouponTemplateSaveReqDTO requestParam) {
        if (ObjectUtil.equal(requestParam.getTarget(), DiscountTargetEnum.PRODUCT_SPECIFIC)) {
            //判断数据库是否存在优惠的商品
            // 调用商品中台验证商品是否存在，如果不存在抛出异常
            // ......
        }
    }

    @Override
    public String mark() {
        return  MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
