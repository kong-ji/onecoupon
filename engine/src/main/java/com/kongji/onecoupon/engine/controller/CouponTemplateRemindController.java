  

package com.kongji.onecoupon.engine.controller;

import com.kongji.onecoupon.engine.common.context.UserContext;
import com.kongji.onecoupon.engine.dto.req.CouponTemplateRemindCancelReqDTO;
import com.kongji.onecoupon.engine.dto.req.CouponTemplateRemindCreateReqDTO;
import com.kongji.onecoupon.engine.dto.req.CouponTemplateRemindQueryReqDTO;
import com.kongji.onecoupon.engine.dto.resp.CouponTemplateRemindQueryRespDTO;
import com.kongji.onecoupon.engine.service.CouponTemplateRemindService;
import com.kongji.onecoupon.framework.idempotent.NoDuplicateSubmit;
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
 * 作者：    kongji
 *    
 *     
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "优惠券预约提醒管理")
public class CouponTemplateRemindController {

    private final CouponTemplateRemindService couponTemplateRemindService;

    @Operation(summary = "发出优惠券预约提醒请求")
    @NoDuplicateSubmit(message = "请勿短时间内重复提交预约提醒请求")
    @PostMapping("/api/engine/coupon-template-remind/create")
    public Result<Void> createCouponRemind(@RequestBody CouponTemplateRemindCreateReqDTO requestParam) {
        couponTemplateRemindService.createCouponRemind(requestParam);
        return Results.success();
    }

    @Operation(summary = "查询优惠券预约提醒")
    @GetMapping("/api/engine/coupon-template-remind/list")
    public Result<List<CouponTemplateRemindQueryRespDTO>> listCouponRemind() {
        return Results.success(couponTemplateRemindService.listCouponRemind(new CouponTemplateRemindQueryReqDTO(UserContext.getUserId())));
    }

    @Operation(summary = "取消优惠券预约提醒")
    @NoDuplicateSubmit(message = "请勿短时间内重复提交取消预约提醒请求")
    @PostMapping("/api/engine/coupon-template-remind/cancel")
    public Result<Void> cancelCouponRemind(@RequestBody CouponTemplateRemindCancelReqDTO requestParam) {
        couponTemplateRemindService.cancelCouponRemind(requestParam);
        return Results.success();
    }
}
