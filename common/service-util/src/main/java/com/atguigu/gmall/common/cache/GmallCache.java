package com.atguigu.gmall.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author:LiuSir
 * @Date: Create in 20:07 2020-11-09
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)//写了Resource 使用反编译时，你的代码没有注解了，即注解不起作用
public @interface GmallCache {
    /**
     * 定义缓存的注解
     */
    public String prefix() default "GmallCache";
}
