package com.wzw.sharding.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author:WangZhiwen
 * @Description:全局配置，降低耦合
 * @Date:2018-3-12
 */
public class GlobalConfig {

    private static ConcurrentMap<String,ShardingConfig> shardingConfigMap = new ConcurrentHashMap<String,ShardingConfig>();


    public synchronized static ShardingConfig getShardingConfig(String taskName){
        return shardingConfigMap.get(taskName);
    }

    public synchronized static void setShardingConfig(String taskName,ShardingConfig shardingConfig){
        shardingConfigMap.put(taskName,shardingConfig);
    }


}
