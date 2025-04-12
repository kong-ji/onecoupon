

package com.kongji.onecoupon.engine;

import com.kongji.onecoupon.engine.dto.req.CouponTemplateRemindQueryReqDTO;
import com.kongji.onecoupon.engine.dto.resp.CouponTemplateRemindQueryRespDTO;
import com.kongji.onecoupon.engine.service.CouponTemplateRemindService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CouponTemplateServiceRemindTests {
    @Resource
    private CouponTemplateRemindService couponTemplateRemindService;

    @Test
    void testQuery() {
        CouponTemplateRemindQueryReqDTO req = new CouponTemplateRemindQueryReqDTO();
        req.setUserId("1810868149847928832");

        List<CouponTemplateRemindQueryRespDTO> resp = couponTemplateRemindService.listCouponRemind(req);
        for (CouponTemplateRemindQueryRespDTO couponTemplateRemindQueryRespDTO : resp) {
            System.out.println(couponTemplateRemindQueryRespDTO.toString());
        }

    }
}
