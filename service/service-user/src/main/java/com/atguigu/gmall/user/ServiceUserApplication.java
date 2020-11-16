package com.atguigu.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author:LiuSir
 * @Date: Create in 14:57 2020-11-16
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"com.atguigu.gmall"})
@EnableFeignClients({"com.atguigu.gmall"})
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class,args);
    }
}
