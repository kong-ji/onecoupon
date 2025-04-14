package org.kongji.chataimodel.AI.dto.query;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
@Data
public class OneCouponQuery {
    @ToolParam(required = true, description = "优惠券模板Id")
    private String couponTemplateId;

    @ToolParam(required = true, description = "商家店铺Id")
    private String shopNumber;
}
