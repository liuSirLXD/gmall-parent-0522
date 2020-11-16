package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author:LiuSir
 * @Date: Create in 8:55 2020-11-16
 * 认证过滤器，
 */
@Component
public class AuthFilter implements GlobalFilter {
    //工具类过滤使用的
    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${authUrls.url}")//配置文件中的路径s
    String antUrls;

    @Autowired
    UserFeignClient userFeignClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //gateway拦截是无差别的拦截，而静态资源不需要拦截，有些东西需要放行，uri可以是请求的全路径
        String uri = request.getURI().toString();

        //放行静态资源
        if (uri.indexOf("passport") != -1 || uri.indexOf(".js") != -1 || uri.indexOf(".css") != -1 || uri.indexOf(".jpg") != -1 || uri.indexOf(".png") != -1 || uri.indexOf(".json") != -1 || uri.indexOf(".ico") != -1) {
            return chain.filter(exchange);
        }

        //黑名单，无论如何都不能让其他的地方访问
        boolean match = antPathMatcher.match("**/admin/**", uri);
        if (match) {
            return out(response, ResultCodeEnum.PERMISSION);
        }

        //白名单，要访问页面必须先登录
        //自定义过滤规则
        //与cas权限中心进行交互
        String[] splitWhiteUrls = antUrls.split(",");//可能有多个页面请求
        //得到token
        String token = getToken(request);
        //验证token
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(token)){
            userInfo = userFeignClient.verify(token);
        }

        for (String splitWhiteUrl : splitWhiteUrls) {
            if (uri.indexOf(splitWhiteUrl) != -1 && userInfo == null) {
                //用户需要登陆，才能进入这个页面，否则踢回登录页面，踢回需要用response
                response.setStatusCode(HttpStatus.SEE_OTHER);//重定向
                //从当前页面跳转到后面的页面
                response.getHeaders().set(HttpHeaders.LOCATION, "http://passport.gmall.com/login.html?originUrl=" + uri);
                Mono<Void> redirectMono = response.setComplete();
                return redirectMono;
            }
        }

        return chain.filter(exchange);
    }

    //得到request的token
    private String getToken(ServerHttpRequest request) {
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        String token = null;

        if (null != cookies && cookies.size() > 0) {
            List<HttpCookie> httpCookies = cookies.get("token");
            token = httpCookies.get(0).getValue();
        }
        return token;
    }

    //重写了response
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        //通用返回结果
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bits);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(wrap));
    }
}
