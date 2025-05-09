  

package com.kongji.onecoupon.engine.toolkit;

import cn.hutool.core.date.DateUtil;
import com.kongji.onecoupon.engine.common.enums.CouponRemindTypeEnum;
import com.kongji.onecoupon.engine.dto.resp.CouponTemplateRemindQueryRespDTO;
import com.kongji.onecoupon.framework.exception.ClientException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 优惠券预约提醒工具类
 * <p>
 * 作者：    kongji
 *    
 *     
 */
public class CouponTemplateRemindUtil {

    /**
     * 下一个类型的位移量，每个类型占用12个bit位，共计60分钟
     */
    private static final int NEXT_TYPE_BITS = 12;

    /**
     * 5分钟为一个间隔
     */
    private static final int TIME_INTERVAL = 5;

    /**
     * 提醒方式的数量
     */
    private static final int TYPE_COUNT = CouponRemindTypeEnum.values().length;

    /**
     * 填充预约信息
     */
    public static void fillRemindInformation(CouponTemplateRemindQueryRespDTO resp, Long information) {
        List<Date> dateList = new ArrayList<>();
        List<String> remindType = new ArrayList<>();
        Date validStartTime = resp.getValidStartTime();
        for (int i = NEXT_TYPE_BITS - 1; i >= 0; i--) {
            // 按时间节点倒叙遍历，即离开抢时间最久，离现在最近
            for (int j = 0; j < TYPE_COUNT; j++) {
                // 对于每个时间节点，遍历所有类型
                if (((information >> (j * NEXT_TYPE_BITS + i)) & 1) == 1) {
                    // 该时间节点的该提醒类型用户有预约
                    Date date = DateUtil.offsetMinute(validStartTime, -((i + 1) * TIME_INTERVAL));
                    dateList.add(date);
                    remindType.add(CouponRemindTypeEnum.getDescribeByType(j));
                }
            }
        }
        resp.setRemindTime(dateList);
        resp.setRemindType(remindType);
    }

    /**
     * 根据预约时间和预约类型计算bitmap
     */
    public static Long calculateBitMap(Integer remindTime, Integer type) {
        if (remindTime > TIME_INTERVAL * NEXT_TYPE_BITS) {
            throw new ClientException("预约提醒的时间不能早于开票前" + TIME_INTERVAL * NEXT_TYPE_BITS + "分钟");
        }
        return 1L << (type * NEXT_TYPE_BITS + Math.max(0, remindTime / TIME_INTERVAL - 1));
    }
}
