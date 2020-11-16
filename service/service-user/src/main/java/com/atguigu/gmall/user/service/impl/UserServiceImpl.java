package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import springfox.documentation.service.ApiListing;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author:LiuSir
 * @Date: Create in 14:56 2020-11-16
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserInfoMapper userInfoMapper;


    @Override
    public Map<String, Object> login(UserInfo userInfo) {

        Map<String,Object> map = null;
        //根据账号名密码验证账号是否登录
        String loginName = userInfo.getLoginName();
        String password = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        //查询用户信息
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("login_name",loginName);
        wrapper.eq("passwd",password);
        //能够用前端传进来的用户名和密码查出数据，说明数据库有用户信息，既可以登录成功
        UserInfo userInfos = userInfoMapper.selectOne(wrapper);

        //将用户信息存入缓存
        if(null != userInfos){
            //生成token
            String token = UUID.randomUUID().toString();

            //存入缓存中
            redisTemplate.opsForValue().set("user:"+token,userInfos.getId());

            //放入map 中
            map = new HashMap<>();
            map.put("name",userInfos.getName());
            map.put("nick_name",userInfos.getNickName());
            map.put("token",token);
        }
        return map;
    }

    //验证token
    @Override
    public UserInfo verify(String token) {
        //
        UserInfo userInfo = null;
        Integer userId = (Integer) redisTemplate.opsForValue().get(RedisConst.USER_KEY_PREFIX + token);

        if(null != userId && userId > 0) {
            userInfo = userInfoMapper.selectById(userId);
        }

        return userInfo;
    }
}
