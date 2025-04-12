

package com.kongji.onecoupon.merchant.admin.dao.sharding;

import lombok.Getter;
import org.apache.shardingsphere.infra.util.exception.ShardingSpherePreconditions;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.apache.shardingsphere.sharding.exception.algorithm.sharding.ShardingAlgorithmInitializationException;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * 基于 HashMod 方式自定义分库算法
 * <p>
 * 作者：kongji
 *    
 *
 */
public final class DBHashModShardingAlgorithm implements StandardShardingAlgorithm<Long> {
    //定义配置文件/分片数量/分片配置名
    @Getter
    private Properties props;
    private int shardingCount;
    private static final String SHARDING_COUNT_KEY = "sharding-count";

    /**
     *
     * @param collection  ShardingSphere 中，availableTargetNames 可能表示不同的层级：
     * 如果是在数据库层级分片： availableTargetNames 会包含所有的数据库名称。此时，shardingCount 就表示数据库的数量。
     * 如果是在表层级分片： availableTargetNames 会包含数据库中的所有表的名称。此时，shardingCount 就表示表的数量。
     * @param preciseShardingValue 分片值
     * @return 对应索引的数据库
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        //获取分片字段id
        long id=preciseShardingValue.getValue();
        //获取数据库的数量
        int dbSize=collection.size();
        //获得哈希取模后的数据库索引
        int mod=(int)hashShardingValue(id)%shardingCount/(shardingCount/dbSize);
        //遍历数据库找对应索引数据库
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
        // 暂无范围分片场景，默认返回空
        return List.of();
    }
    //手动重写StandardShardingAlgorithm类的init()函数
    @Override
    public void init(final Properties properties) {
        //初始化
        this.props = properties;
        shardingCount=getShardingCount(properties);
    }
    private int getShardingCount(final Properties properties) {
        ShardingSpherePreconditions.checkState(props.containsKey(SHARDING_COUNT_KEY), () -> new ShardingAlgorithmInitializationException(getType(), "Sharding count cannot be null."));
        return Integer.parseInt(properties.getProperty(SHARDING_COUNT_KEY));
    }
    private long hashShardingValue(final Comparable<?> shardingValue) {
        return Math.abs((long) shardingValue.hashCode());
    }
}