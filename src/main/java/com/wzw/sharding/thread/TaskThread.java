package com.wzw.sharding.thread;

import com.wzw.sharding.config.GlobalConfig;
import com.wzw.sharding.config.ShardingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
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

        int lineNum = 0;
        String line = null;
        try {

            fis = new FileInputStream(dataFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            while ((line=br.readLine())!=null) {
                method.invoke(obj,line);
                lineNum++;//当前处理到第lineNum行

                if(lineNum%breakpointSkip==0){

                }
            }

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

    private void updateBreakpoint(){
        
    }


}
