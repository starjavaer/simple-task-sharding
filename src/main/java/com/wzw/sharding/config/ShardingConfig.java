package com.wzw.sharding.config;

/**
 * @Author:WangZhiwen
 * @Description:分片配置
 * @Date:2018-3-8
 */
public class ShardingConfig<T> {

    private Integer shardingSize;//分片大小

    private Integer threadCount;//线程数量

    private String tempPath;//临时文件路径

    private Integer breakpointSkip;//断点

    private Class<T> targetClazz;//目标类

    private String executeMode;//执行模式 1.



    public Integer getShardingSize() {
        return shardingSize;
    }

    public ShardingConfig<T> setShardingSize(Integer shardingSize) {
        this.shardingSize = shardingSize;
        return this;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public ShardingConfig<T> setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public String getTempPath() {
        return tempPath;
    }

    public ShardingConfig<T> setTempPath(String tempPath) {
        this.tempPath = tempPath;
        return this;
    }

    public Integer getBreakpointSkip() {
        return breakpointSkip;
    }

    public ShardingConfig<T> setBreakpointSkip(Integer breakpointSkip) {
        this.breakpointSkip = breakpointSkip;
        return this;
    }

    public Class<T> getTargetClazz() {
        return targetClazz;
    }

    public ShardingConfig<T> setTargetClazz(Class<T> targetClazz) {
        this.targetClazz = targetClazz;
        return this;
    }


}
