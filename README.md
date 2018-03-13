# simple-task-sharding

## Introduction

simple-task-sharding是一个体型小巧的任务分片框架，该框架旨在提高大数据量的任务执行效率，框架内部会对数据列表进行分片和断点处理，并启动多个线程并发执行任务，框架的优点是使用简单，且对业务逻辑代码的侵入性较低。

ps：当前版本还不稳定，暂时仅具参考价值。

## Usage

1.使用@ShardingTask注解需要执行的方法，该方法将会被任务调用。

``` java
@ShardingTask("testTask")
public void test(String param){
    System.out.println(">>>>>> "+param);
}
```

2.创建ShardingConfig对象并初始化分片配置项。

``` java
ShardingConfig config = new ShardingConfig();
config.setTargetClazz(Test.class)
      .setShardingSize(10000)
      .setTempPath("D:\\")
      .setThreadCount(8)
      .setBreakpointSkip(100);
```

3.创建执行器并注入分片配置。

``` java
TaskExecutor executor = new TaskExecutor("testTask");
executor.setShardingConfig(config);
```

4.这里模拟10w条数据。

``` java
List<String> dataList = new ArrayList<String>();
for(int i=1;i<100000;i++){
  dataList.add(String.valueOf(i));
}
```

5.向任务执行器中注入数据源并启动任务执行器。

``` java
executor.execute(dataList);
```




