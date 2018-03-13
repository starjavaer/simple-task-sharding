package com.wzw.sharding.handler;

import com.wzw.sharding.config.GlobalConfig;
import com.wzw.sharding.config.ShardingConfig;
import com.wzw.sharding.constants.ExecuteMode;
import com.wzw.sharding.thread.ShardingThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wzw.sharding.config.GlobalConfig.getShardingConfig;

/**
 * @Author:WangZhiwen
 * @Description:
 * @Date:2018-2-6
 */
public class ShardingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardingHandler.class);

    private String taskName;

    public void handle(List<String> dataList){

        ShardingConfig shardingConfig = GlobalConfig.getShardingConfig(taskName);

        if(shardingConfig.getExecuteMode().getValue()==ExecuteMode.RESTART.getValue()){
            initTempDir(taskName);
            initTempFile(taskName,dataList);
        }

    }

    /**
     * 初始化临时文件夹
     * @param taskName
     */
    private void initTempDir(String taskName){

        ShardingConfig shardingConfig = getShardingConfig(taskName);

        try {

            String tempPath = shardingConfig.getTempPath();

            String tempTaskDirUrl = tempPath+taskName;//任务的临时文件目录

            String tempTaskDataDirUrl = tempTaskDirUrl+"/data/";//任务的临时数据文件目录
            File file2 = new File(tempTaskDataDirUrl);
            file2.mkdirs();

            String tempTaskBreakpointDirUrl = tempTaskDirUrl+"/breakpoint/";//任务的临时断点文件目录
            File file3 = new File(tempTaskBreakpointDirUrl);
            file3.mkdirs();

        } catch (Exception e) {
            LOGGER.error("初始化临时文件夹异常",e);
        }

    }

    /**
     * 初始化数据文件和断点文件
     * @param taskName
     * @param dataList
     */
    private void initTempFile(String taskName,List<String> dataList){

        ShardingConfig shardingConfig = getShardingConfig(taskName);

        try {

            Integer shardingSize = shardingConfig.getShardingSize();

            //计算分片数
            Integer dataCount = dataList.size();
            Integer shardingCount = dataCount%shardingSize==0?dataCount/shardingSize:(dataCount/shardingSize+1);

            ExecutorService executorService = Executors.newCachedThreadPool();

            for(int i=1;i<=shardingCount;i++){

                int from = (i-1)*shardingSize;
                int to = i*shardingSize<dataCount?i*shardingSize:dataCount;

                List<String> dataLines = dataList.subList(from,to);
                executorService.submit(new ShardingThread(taskName,i,dataLines));

            }

            executorService.shutdown();

            while(true){
                if(executorService.isTerminated()){
                    break;
                }
                Thread.sleep(100);
            }

        } catch (Exception e) {
            LOGGER.error("初始化临时文件异常",e);
        }

    }

    public ShardingHandler(String taskName) {
        this.taskName = taskName;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        for(int i=1;i<=100;i++){
            list.add(String.valueOf(i));
        }

        System.out.println(list.subList(0,100));
    }


}
