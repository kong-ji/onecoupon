package org.kongji.chataimodel.AI.tools;

import cn.hutool.core.bean.BeanUtil;
import com.kongji.onecoupon.framework.result.Result;
import lombok.RequiredArgsConstructor;
import org.kongji.chataimodel.AI.dto.query.OneCouponQuery;
import org.kongji.chataimodel.AI.dto.query.OneCouponRemind;
import org.kongji.chataimodel.AI.dto.req.CouponTemplateQueryReqDTO;
import org.kongji.chataimodel.AI.dto.req.CouponTemplateRemindCancelReqDTO;
import org.kongji.chataimodel.AI.dto.req.CouponTemplateRemindCreateReqDTO;
import org.kongji.chataimodel.AI.dto.resp.CouponTemplateQueryRespDTO;
import org.kongji.chataimodel.AI.dto.resp.CouponTemplateRemindQueryRespDTO;
import org.kongji.chataimodel.dao.entity.CouponTemplateDO;
import org.kongji.chataimodel.feign.OneCouponTemplateClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
@Component
@RequiredArgsConstructor
public class OneCouponTool {
    private final OneCouponTemplateClient oneCouponTemplateClient;

    @Tool(description = "根据传入的优惠券模板id和商家店铺id查询优惠券模板")
    public Result<CouponTemplateQueryRespDTO> queryOneCouponTemplate(@ToolParam(description = "查询的条件") OneCouponQuery query) {

        return oneCouponTemplateClient.getOneCouponTemplate(
                new CouponTemplateQueryReqDTO(query.getShopNumber(), query.getCouponTemplateId()));
    }

    @Tool(description = "推荐的时候去查询全部优惠券，不需要参数")
    public Result<List<CouponTemplateQueryRespDTO>> queryAllCouponTemplate() {

        return oneCouponTemplateClient.findOneCouponTemplate();
    }

    @Tool(description = "设置预约优惠券提醒")
    public Result<Void> createCouponRemind(@ToolParam(description = "设置提醒的参数条件")OneCouponRemind oneCouponRemind){

        return oneCouponTemplateClient.createCouponRemind(BeanUtil.copyProperties(oneCouponRemind, CouponTemplateRemindCreateReqDTO.class));
    }

    @Tool(description = "取消预约优惠券提醒的一个时间点")
    public Result<Void> cancelCouponRemind(@ToolParam(description = "取消提醒参数")OneCouponRemind oneCouponRemind){
        return oneCouponTemplateClient.cancelCouponRemind(BeanUtil.copyProperties(oneCouponRemind, CouponTemplateRemindCancelReqDTO.class));
    }

    @Tool(description = "查询一个优惠券已经提醒的所有时间点")
    Result<List<CouponTemplateRemindQueryRespDTO>> listCouponRemind(){
        return oneCouponTemplateClient.listCouponRemind();
    }
}
