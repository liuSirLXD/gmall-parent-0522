package com.atguigu.gmall.user.client;

import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author:LiuSir
 * @Date: Create in 18:36 2020-11-16
 */
@FeignClient(value = "service-user")
public interface UserFeignClient {

    @RequestMapping("api/user/passport/verify/{token}")
    UserInfo verify(@PathVariable("token") String token);
}
