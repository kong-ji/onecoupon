  

package com.kongji.onecoupon.distribution.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券模板任务执行事件
 * <p>
 * 作者：kongji
 *    
 *     
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateDistributionEvent {

    /**
     * 优惠券分发任务id
     */
    private Long couponTaskId;

    /**
     * 优惠券分发任务批量id
     */
    private Long couponTaskBatchId;

    /**
     * 店铺编号
     */
    private Long shopNumber;

    /**
     * 优惠券模板id
     */
    private Long couponTemplateId;

    /**
     * 消耗规则
     */
    private String couponTemplateConsumeRule;

    /**
     * 批量保存用户优惠券 Set 长度，默认满 5000 才会批量保存数据库
     */
    private Integer batchUserSetSize;

    /**
     * 分发结束标识
     */
    private Boolean distributionEndFlag;
}
