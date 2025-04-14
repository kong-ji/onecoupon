package org.kongji.chataimodel.AI.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
@Data
public class OneCouponRemind {
    /**
     * 优惠券模板id
     */
    @ToolParam(required = true, description = "优惠券模板Id")
    private String couponTemplateId;

    /**
     * 店铺编号
     */
    @ToolParam(required = true, description = "商家店铺Id")
    private String shopNumber;

    /**
     * 提醒方式
     */
    @ToolParam(required = false, description = "提醒方式")
    private Integer type;

    /**
     * 提醒时间，比如五分钟，十分钟，十五分钟
     */
    @ToolParam(required = true, description = "提前提醒时间，5分钟、10分钟...60分钟")
    private Integer remindTime;
}
