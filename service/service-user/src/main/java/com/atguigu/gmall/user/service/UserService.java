package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

import java.util.Map;

/**
 * @Author:LiuSir
 * @Date: Create in 14:56 2020-11-16
 */
public interface UserService {
    Map<String, Object> login(UserInfo userInfo);

    UserInfo verify(String token);
}
