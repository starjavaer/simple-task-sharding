package com.wzw.sharding.thread;

import com.wzw.sharding.config.GlobalConfig;
import com.wzw.sharding.config.ShardingConfig;
import com.wzw.sharding.utils.SimpleFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Author:WangZhiwen
 * @Description:
 * @Date:2018-3-11
 */
public class TaskThread implements Callable<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskThread.class);

    private String taskName;

    private Integer shardingIndex;

    private Object obj;

    private Method method;

    @Override
    public String call() throws Exception {

        Long t1 = System.currentTimeMillis();

        ShardingConfig shardingConfig = GlobalConfig.getShardingConfig(taskName);

        String dataFileUrl = shardingConfig.getTempPath()+taskName+"/data/data"+shardingIndex+".txt";

        Integer breakpointSkip = shardingConfig.getBreakpointSkip();

        File dataFile = new File(dataFileUrl);

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        Integer lineNum = 0;

        Integer breakpointValue = getBreakpoint();

        String line = null;

        try {

            fis = new FileInputStream(dataFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            while ((line=br.readLine())!=null) {

                lineNum++;//当前开始处理到第lineNum行

                if(lineNum<=breakpointValue){
                    continue;
                }

                method.invoke(obj,line);

                if(lineNum%breakpointSkip==0){
                    updateBreakpoint(lineNum);
                }
            }

            updateBreakpoint(lineNum);

        } catch (Exception e) {
            LOGGER.error("任务[param:{}]执行异常",line,e);
        } finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Long t2 = System.currentTimeMillis();

        return Thread.currentThread().getName()+"结束执行，耗时："+(t2-t1)+"ms";
    }

    public TaskThread(String taskName,Integer shardingIndex,Object obj,Method method) {
        this.taskName = taskName;
        this.shardingIndex = shardingIndex;
        this.obj = obj;
        this.method = method;
    }

    /**
     * 获取断点
     * @return
     */
    private Integer getBreakpoint(){

        ShardingConfig shardingConfig = GlobalConfig.getShardingConfig(taskName);

        String breakpointFileUrl = shardingConfig.getTempPath()+taskName+"/breakpoint/data"+shardingIndex+".txt";

        SimpleFile breakpointFile = new SimpleFile(breakpointFileUrl);
        String line = breakpointFile.readLine(1);

        return Integer.valueOf(line);

    }

    /**
     * 更新断点
     * @param lineNum
     */
    private void updateBreakpoint(Integer lineNum){

        ShardingConfig shardingConfig = GlobalConfig.getShardingConfig(taskName);

        String breakpointFileUrl = shardingConfig.getTempPath()+taskName+"/breakpoint/data"+shardingIndex+".txt";

        SimpleFile breakpointFile = new SimpleFile(breakpointFileUrl);

        List<String> lines = new ArrayList<String>();
        lines.add(String.valueOf(lineNum));
        breakpointFile.writeLines(lines);
        
    }


}
