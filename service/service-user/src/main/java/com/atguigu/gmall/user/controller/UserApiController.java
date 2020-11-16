package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author:LiuSir
 * @Date: Create in 14:52 2020-11-16
 */
@RestController
@RequestMapping("api/user/passport")
public class UserApiController {
    @Autowired
    UserService userService;

    @RequestMapping("verify/{token}")
    UserInfo verify(@PathVariable("token") String token){
        UserInfo userInfo = userService.verify(token);
        return userInfo;
    }

    @RequestMapping("login")
    public Result login(@RequestBody UserInfo userInfo){

        Map<String,Object> map = userService.login(userInfo);

        //判断map是不是为空，为空登录失败，不为空登录成功
        if(null != map){
            return Result.ok(map);//登录成功
        }else {
            return Result.fail();//登录失败
        }
    }
}
