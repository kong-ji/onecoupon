

package com.kongji.onecoupon.merchant.admin.controller;

import com.kongji.onecoupon.framework.idempotent.NoDuplicateSubmit;
import com.kongji.onecoupon.framework.result.Result;
import com.kongji.onecoupon.framework.web.Results;
import com.kongji.onecoupon.merchant.admin.dto.req.CouponTaskCreateReqDTO;
import com.kongji.onecoupon.merchant.admin.service.CouponTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券推送任务控制层
 * <p>
 * 作者：kongji
 *    
 *
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "优惠券推送任务管理")
public class CouponTaskController {

    private final CouponTaskService couponTaskService;

    @Operation(summary = "创建优惠券推送任务")
    @NoDuplicateSubmit(message = "请勿短时间内重复提交优惠券推送任务")
    @PostMapping("/api/merchant-admin/coupon-task/create")
    public Result<Void> createCouponTask(@RequestBody CouponTaskCreateReqDTO requestParam) {
        couponTaskService.createCouponTask(requestParam);
        return Results.success();
    }
}
