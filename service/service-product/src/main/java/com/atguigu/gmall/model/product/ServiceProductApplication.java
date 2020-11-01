package com.atguigu.gmall.model.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 11:18 2020-10-31
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.gmall"})
public class ServiceProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class,args);
    }
}
