package com.wzw.sharding.annotation;

import java.lang.annotation.*;

/**
 * @Author:WangZhiwen
 * @Description:
 * @Date:2018-3-8
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ShardingTask {

    String value() default "分片任务名称";

}
