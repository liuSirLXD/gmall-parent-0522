package com.atguigu.gmall.gateway.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

/**
 * @Author:LiuSir
 * @Date: Create in 14:13 2020-11-16
 * 配置网关的跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        //跨域配置对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");//设置允许访问的网络
        corsConfiguration.setAllowCredentials(true);//允许从服务器获取cookies
        corsConfiguration.addAllowedMethod("*");//设置请求方法 * 表示 任意
        corsConfiguration.addAllowedHeader("*");//设置请求头信息 * 表示 任意

        //配置源对象
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);

        //cors 过滤器对象
        return new CorsWebFilter(corsConfigurationSource);

    }
}
