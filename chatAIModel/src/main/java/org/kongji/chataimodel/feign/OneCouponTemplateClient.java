package org.kongji.chataimodel.feign;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */


import com.kongji.onecoupon.framework.result.Result;
import com.kongji.onecoupon.framework.web.Results;
import org.kongji.chataimodel.AI.dto.req.CouponTemplateQueryReqDTO;
import org.kongji.chataimodel.AI.dto.req.CouponTemplateRemindCancelReqDTO;
import org.kongji.chataimodel.AI.dto.req.CouponTemplateRemindCreateReqDTO;
import org.kongji.chataimodel.AI.dto.resp.CouponTemplateQueryRespDTO;
import org.kongji.chataimodel.AI.dto.resp.CouponTemplateRemindQueryRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 优惠券模板远程调用
 */
@FeignClient(name = "oneCoupon-enginekongji")
public interface OneCouponTemplateClient {

    @PostMapping("/api/engine/coupon-template/query")
    Result<CouponTemplateQueryRespDTO> getOneCouponTemplate(@RequestBody CouponTemplateQueryReqDTO couponTemplateQueryReqDTO);

    @GetMapping("/api/engine/coupon-template/query/all")
    Result<List<CouponTemplateQueryRespDTO>>findOneCouponTemplate();

    @PostMapping("/api/engine/coupon-template-remind/create")
    Result<Void> createCouponRemind(@RequestBody CouponTemplateRemindCreateReqDTO requestParam);

    @GetMapping("/api/engine/coupon-template-remind/list")
    Result<List<CouponTemplateRemindQueryRespDTO>> listCouponRemind();

    @PostMapping("/api/engine/coupon-template-remind/cancel")
    Result<Void> cancelCouponRemind(@RequestBody CouponTemplateRemindCancelReqDTO requestParam);
}
