package com.wzw.sharding.core;

import com.wzw.sharding.annotation.ShardingTask;
import com.wzw.sharding.config.GlobalConfig;
import com.wzw.sharding.config.ShardingConfig;
import com.wzw.sharding.constants.ExecuteMode;
import com.wzw.sharding.handler.ShardingHandler;
import com.wzw.sharding.thread.TaskThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author:WangZhiwen
 * @Description:
 * @Date:2018-3-8
 */
public class TaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutor.class);

    private String taskName;

    private ShardingConfig shardingConfig;

    /**
     * 任务执行启动方法
     * @throws Exception
     */
    public void execute(List<String> dataList) {

        try {
            //校验分片配置
            checkShardingConfig(shardingConfig);

            //将分片配置实例放进全局配置
            GlobalConfig.setShardingConfig(taskName,shardingConfig);

            //获取目标类
            Class targetClazz = shardingConfig.getTargetClazz();

            //定位目标任务（task/method）
            final Object obj = targetClazz.newInstance();
            final Method method = findTask(targetClazz,taskName);

            //处理分片源
            ShardingHandler shardingHandler = new ShardingHandler(taskName);
            shardingHandler.handle(dataList);

            if(method!=null){

                Integer shardingSize = shardingConfig.getShardingSize();
                Integer threadCount = shardingConfig.getThreadCount();

                ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

                Integer dataCount = dataList.size();
                Integer shardingCount = dataCount%shardingSize==0?dataCount/shardingSize:(dataCount/shardingSize+1);

                for(int i=1;i<=shardingCount;i++){
                    Future future = executorService.submit(new TaskThread(taskName,i,obj,method));
                    System.out.println(future.get());
                }

                executorService.shutdown();


            }else{
                LOGGER.error("未找到指定名称的任务方法");
            }
        } catch (Exception e) {
            LOGGER.error("任务执行异常",e);
        }

    }

    private void checkShardingConfig(ShardingConfig shardingConfig){

        if(shardingConfig==null){
            LOGGER.error("未设置分片配置");
            return;
        }

        if(shardingConfig.getTargetClazz()==null){
            LOGGER.error("未设置目标类");
            return;
        }

        String tempPath = shardingConfig.getTempPath();
        if(!tempPath.endsWith("/")){
            tempPath = tempPath+"/";
            shardingConfig.setTempPath(tempPath);
        }

        if(shardingConfig.getExecuteMode()==null){
            shardingConfig.setExecuteMode(ExecuteMode.RESTART);
        }

    }

    /**
     * 定位对应任务的方法
     * @param clazz
     * @param taskName
     * @return
     */
    private Method findTask(Class clazz,String taskName){
        Method task = null;
        try {
            Method[] methods = clazz.getDeclaredMethods();
            for(Method method:methods){
                if(method.isAnnotationPresent(ShardingTask.class)&&method.getAnnotation(ShardingTask.class).value().equals(taskName)){
                    task = method;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }



    public ShardingConfig getShardingConfig() {
        return shardingConfig;
    }

    public TaskExecutor setShardingConfig(ShardingConfig shardingConfig) {
        this.shardingConfig = shardingConfig;
        return this;
    }

    public TaskExecutor(String taskName) {
        this.taskName = taskName;
    }


}
