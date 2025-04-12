

package com.kongji.onecoupon.engine.sharding;

import org.junit.jupiter.api.Test;

/**
 * 用户优惠券分片单元测试
 * <p>
 * 作者：kongji
 *     
 *     
 */
public class UserCouponLogShardingTests {

    public static final String SQL = """
            CREATE TABLE `t_user_coupon_log_%d` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
              `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
              `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
              `operation_log` text COMMENT '操作日志',
              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券操作日志表';
            """;

    @Test
    public void sharding0Test() {
        for (int i = 0; i < 16; i++) {
            System.out.println(String.format(SQL, i));
        }
    }

    @Test
    public void sharding1Test() {
        for (int i = 16; i < 32; i++) {
            System.out.println(String.format(SQL, i));
        }
    }
}
