package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 11:15 2020-11-03
 */
@Configuration
public class ServiceProductConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
