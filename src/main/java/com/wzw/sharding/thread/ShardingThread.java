package com.wzw.sharding.thread;

import com.wzw.sharding.config.GlobalConfig;
import com.wzw.sharding.config.ShardingConfig;
import com.wzw.sharding.utils.SimpleFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Author:WangZhiwen
 * @Description:
 * @Date:2018-3-12
 */
public class ShardingThread implements Callable<String> {

    private String taskName;

    private Integer shardingIndex;

    private List<String> lines;

    @Override
    public String call() throws Exception {

        ShardingConfig shardingConfig = GlobalConfig.getShardingConfig(taskName);

        String dataFileUrl = shardingConfig.getTempPath()+taskName+"/data/data"+shardingIndex+".txt";
        String breakpointFileUrl = shardingConfig.getTempPath()+taskName+"/breakpoint/data"+shardingIndex+".txt";

        SimpleFile dataFile = new SimpleFile(dataFileUrl);
        dataFile.writeLines(lines);

        List<String> breakpointLines = new ArrayList<String>();
        breakpointLines.add("0");

        SimpleFile breakpointFile = new SimpleFile(breakpointFileUrl);
        breakpointFile.writeLines(breakpointLines);

        return null;
    }

    public ShardingThread(String taskName,Integer shardingIndex,List<String> lines) {
        this.taskName = taskName;
        this.shardingIndex = shardingIndex;
        this.lines = lines;
    }

}
