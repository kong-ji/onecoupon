

package com.kongji.onecoupon.engine.sharding;

import org.junit.jupiter.api.Test;

/**
 * 用户优惠券分片单元测试
 * <p>
 * 作者：kongji
 *     
 *     
 */
public class UserCouponShardingTests {

    public static final String SQL = """
            CREATE TABLE `t_user_coupon_%d` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
              `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
              `coupon_template_id` bigint(20) DEFAULT NULL COMMENT '优惠券模板ID',
              `receive_time` datetime DEFAULT NULL COMMENT '领取时间',
              `receive_count` int(3) DEFAULT NULL COMMENT '领取次数',
              `valid_start_time` datetime DEFAULT NULL COMMENT '有效期开始时间',
              `valid_end_time` datetime DEFAULT NULL COMMENT '有效期结束时间',
              `use_time` datetime DEFAULT NULL COMMENT '使用时间',
              `source` tinyint(1) DEFAULT NULL COMMENT '券来源 0：领券中心 1：平台发放 2：店铺领取',
              `status` tinyint(1) DEFAULT NULL COMMENT '状态 0：未使用 1：锁定 2：已使用 3：已过期 4：已撤回',
              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
              `update_time` datetime DEFAULT NULL COMMENT '修改时间',
              `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
              PRIMARY KEY (`id`),
              UNIQUE KEY `idx_user_id_coupon_template_receive_count` (`user_id`,`coupon_template_id`,`receive_count`) USING BTREE,
              KEY `idx_user_id` (`user_id`) USING BTREE
            ) ENGINE=InnoDB AUTO_INCREMENT=1815640588360376337 DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';
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
