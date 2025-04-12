  

package com.kongji.onecoupon.engine;

import cn.hutool.core.bean.BeanUtil;
import com.kongji.onecoupon.engine.dao.entity.CouponTemplateRemindDO;
import com.kongji.onecoupon.engine.dto.req.CouponTemplateRemindCreateReqDTO;
import com.kongji.onecoupon.engine.mq.event.CouponTemplateRemindDelayEvent;

public class CouponRemindCreateBeanConvertTest {

    public static void main(String[] args) {
        CouponTemplateRemindCreateReqDTO dto = new CouponTemplateRemindCreateReqDTO();
        dto.setCouponTemplateId("111");
        dto.setShopNumber("222");
        dto.setType(1);
        dto.setRemindTime(10);

        CouponTemplateRemindDO couponTemplateRemindDO = BeanUtil.toBean(dto, CouponTemplateRemindDO.class);
        System.out.println(couponTemplateRemindDO.toString());

        CouponTemplateRemindDelayEvent couponRemindEvent = BeanUtil.toBean(dto, CouponTemplateRemindDelayEvent.class);
        System.out.println(couponRemindEvent.toString());
    }
}
