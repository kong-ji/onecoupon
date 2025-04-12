

package com.kongji.onecoupon.engine.sharding;

import org.junit.jupiter.api.Test;

/**
 * 优惠券结算分片单元测试
 * <p>
 * 作者：kongji
 *     
 * 
 */
public class CouponSettlementShardingTests {

    public static final String SQL = """
            CREATE TABLE `t_coupon_settlement_%d` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
              `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
              `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
              `coupon_id` bigint(20) DEFAULT NULL COMMENT '优惠券ID',
              `status` int(11) DEFAULT NULL COMMENT '结算单状态 0：锁定 1：已取消 2：已支付 3：已退款',
              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
              `update_time` datetime DEFAULT NULL COMMENT '修改时间',
              PRIMARY KEY (`id`),
              KEY `idx_user_id` (`user_id`) USING BTREE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券结算单表';
            """;

    @Test
    public void sharding0Test() {
        for (int i = 0; i < 8; i++) {
            System.out.println(String.format(SQL, i));
        }
    }

    @Test
    public void sharding1Test() {
        for (int i = 8; i < 16; i++) {
            System.out.println(String.format(SQL, i));
        }
    }
}
