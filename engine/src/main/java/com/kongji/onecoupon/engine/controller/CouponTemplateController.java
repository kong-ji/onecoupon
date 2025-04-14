

package com.kongji.onecoupon.engine.controller;

import com.kongji.onecoupon.engine.dto.req.CouponTemplateQueryReqDTO;
import com.kongji.onecoupon.engine.dto.resp.CouponTemplateQueryRespDTO;
import com.kongji.onecoupon.engine.service.CouponTemplateService;
import com.kongji.onecoupon.framework.result.Result;
import com.kongji.onecoupon.framework.web.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 优惠券模板控制层
 * <p>
 * 作者：kongji
 *    
 *     
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "优惠券模板管理")
public class CouponTemplateController {

    private final CouponTemplateService couponTemplateService;

    @Operation(summary = "查询优惠券模板")
    @PostMapping("/api/engine/coupon-template/query")
    public Result<CouponTemplateQueryRespDTO> findCouponTemplate(@RequestBody CouponTemplateQueryReqDTO requestParam) {
        return Results.success(couponTemplateService.findCouponTemplate(requestParam));
    }

    @Operation(summary = "主页默认查询优惠券模板")
    @GetMapping("/api/engine/coupon-template/query/all")
    public Result<List<CouponTemplateQueryRespDTO>> findAllCouponTemplate() {
        return Results.success(couponTemplateService.findAllCouponTemplate());
    }
}
