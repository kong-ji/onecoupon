  

package com.kongji.onecoupon.merchant.admin.service.handler.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.kongji.onecoupon.framework.exception.ClientException;
import com.kongji.onecoupon.merchant.admin.common.enums.DiscountTargetEnum;
import com.kongji.onecoupon.merchant.admin.common.enums.DiscountTypeEnum;
import com.kongji.onecoupon.merchant.admin.dao.entity.CouponTemplateDO;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import com.kongji.onecoupon.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;
import com.kongji.onecoupon.merchant.admin.service.CouponTemplateService;
import com.kongji.onecoupon.merchant.admin.service.basics.chain.MerchantAdminAbstractChainHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

import static com.kongji.onecoupon.merchant.admin.common.enums.ChainBizMarkEnum.MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY;
@Component
public class CouponTemplateCreateParamBaseVerifyChainFilter implements MerchantAdminAbstractChainHandler<CouponTemplateSaveReqDTO> {
    private final int maxStock=200000;

    @Override
    public void handler(CouponTemplateSaveReqDTO requestParam) {
        //检验优惠对象
        boolean targetResult = Arrays.stream(DiscountTargetEnum.values()).anyMatch(
                each -> each.getType() == requestParam.getTarget()
        );
        if (!targetResult) {
            throw new ClientException("优惠对象不存在");
        }
        if(ObjectUtil.equal(requestParam.getTarget(),DiscountTargetEnum.ALL_STORE_GENERAL)
        && StrUtil.isNotEmpty(requestParam.getGoods())){
            throw new ClientException("优惠券全店通用不可设置指定商品");
        }
        if(ObjectUtil.equal(requestParam.getTarget(),DiscountTargetEnum.PRODUCT_SPECIFIC)
        &&StrUtil.isEmpty(requestParam.getGoods())){
            throw new ClientException("优惠券商品专属未设置指定商品");
        }
        //检验优惠类型
        boolean typeResult = Arrays.stream(DiscountTypeEnum.values()).anyMatch(
                each -> each.getType() == requestParam.getType()
        );
        if(!typeResult){
            throw new ClientException("优惠类型不存在");
        }
        //检验活动时间
        Date date=new Date();
        if(requestParam.getValidStartTime().before(date)){
            //方便测试
            //throw new ClientException("有效期开始时间不能早于当前时间");
        }
        if(requestParam.getValidEndTime().before(requestParam.getValidStartTime())){
            throw new ClientException("有效期开始时间不能晚于有效期结束时间");
        }
        //检验库存
        if (requestParam.getStock() <= 0 || requestParam.getStock() > maxStock) {
            // 此处已经基本能判断数据请求属于恶意攻击，可以上报风控中心进行封禁账号
            throw new ClientException("库存数量设置异常");
        }
        //检验规则
        if (!JSON.isValid(requestParam.getReceiveRule())) {
            // 此处已经基本能判断数据请求属于恶意攻击，可以上报风控中心进行封禁账号
            throw new ClientException("领取规则格式错误");
        }
        if (!JSON.isValid(requestParam.getConsumeRule())) {
            // 此处已经基本能判断数据请求属于恶意攻击，可以上报风控中心进行封禁账号
            throw new ClientException("消耗规则格式错误");
        }
    }

    @Override
    public String mark() {
        return MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY.name();
    }

    @Override
    public int getOrder() {
        return 10;
    }
}