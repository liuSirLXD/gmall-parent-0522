package com.atguigu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 19:17 2020-10-30
 */
@SpringBootApplication
@MapperScan("{com.atguigu.gmall}")
public class ServiceTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTestApplication.class,args);
    }
}
