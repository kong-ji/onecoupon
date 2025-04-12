

package com.kongji.onecoupon.merchant.admin.dao.sharding;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.List;

/**
 * 基于 HashMod 方式自定义分表算法
 * <p>
 * 作者：kongji
 *    
 *
 */
public final class TableHashModShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    /**
     *
     * @param collection  ShardingSphere 中，availableTargetNames 可能表示不同的层级：
     * 如果是在数据库层级分片： availableTargetNames 会包含所有的数据库名称。此时，shardingCount 就表示数据库的数量。
     * 如果是在表层级分片： availableTargetNames 会包含数据库中的所有表的名称。此时，shardingCount 就表示表的数量。
     * @param preciseShardingValue 分片值
     * @return  对应索引的数据表
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        //获取分片值
        long id=preciseShardingValue.getValue();
        //获取一个库的表数量
        int tableSize = collection.size();
        //获得哈希取模后的数据表索引
        int mod=(int)hashShardingValue(id)%tableSize;
        //遍历数据库找对应索引数据表
        //index用于匹配索引
        int index=0;
        for(String each:collection){
            if(index==mod){
                return each;
            }
            index++;
        }
        throw new IllegalArgumentException("No target found for value: " + id);
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        return List.of();
    }
    private long hashShardingValue(final Comparable<?> shardingValue) {
        return Math.abs((long) shardingValue.hashCode());
    }
}