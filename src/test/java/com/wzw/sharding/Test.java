package com.wzw.sharding;

import com.wzw.sharding.annotation.ShardingTask;
import com.wzw.sharding.config.ShardingConfig;
import com.wzw.sharding.constants.ExecuteMode;
import com.wzw.sharding.core.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:WangZhiwen
 * @Description:
 * @Date:2018-3-8
 */
public class Test {

    public static void main(String[] args) throws Exception {

        ShardingConfig config = new ShardingConfig();
        config.setTargetClazz(Test.class)
                .setShardingSize(10000)
                .setTempPath("D:\\")
                .setThreadCount(8)
                .setBreakpointSkip(100)
                .setExecuteMode(ExecuteMode.CONTINUE);

        TaskExecutor executor = new TaskExecutor("testTask");
        executor.setShardingConfig(config);

        List<String> dataList = new ArrayList<String>();
        for(int i=1;i<100000;i++){
            dataList.add(String.valueOf(i));
        }

        executor.execute(dataList);


    }

    @ShardingTask("testTask")
    public void test(String param){
        System.out.println(">>>>>> "+param);
    }





}
