package com.atguigu.gmall.common.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @Author:LiuSir
 * @Date: Create in 20:06 2020-11-09
 */
@Component
@Aspect
public class GmallCacheAspect {
    @Autowired
    RedisTemplate redisTemplate;

    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        //声明一个对象Object
        Object object = new Object();

        //返回值类型，参数，缓存前后缀都不知道，然后
        //通过连接点调用过程可以得到方法的各种信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        //得到方法的返回值类型
        Class returnType = signature.getReturnType();
        //得到方法的注解信息
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);
        String cacheKey = annotation.prefix();//得到在缓存注解写的动态前缀
        //查询缓存
        Object[] args = point.getArgs();//有多个参数的情况下
        for (Object arg : args) {
            cacheKey = cacheKey + ":" + arg;
        }
        object = redisTemplate.opsForValue().get(cacheKey);
        if(null==object){
            //设置锁
            String lock = UUID.randomUUID().toString();
            //设置锁和过期时间
            Boolean ifDB = redisTemplate.opsForValue().setIfAbsent(cacheKey+":lock",lock,1, TimeUnit.SECONDS);
            if(ifDB){
                //访问DB
                try {
                    object = point.proceed();//执行切入点的原始方法
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                if(null!=object){
                    //同步缓存
                    redisTemplate.opsForValue().set(cacheKey,object);
                    String script = "if redis.call('get',KEYS[1]) == ARGV[1] then redis.call('del',KEYS[1]) else return 0 end";
                    //设置lua脚本返回数据类型
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    //设置lua脚本返回数据类型为long
                    redisScript.setResultType(Long.class);
                    redisScript.setScriptText(script);
                    redisTemplate.execute(redisScript, Arrays.asList(cacheKey+":lock"),lock);

                }
            }else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }
}
