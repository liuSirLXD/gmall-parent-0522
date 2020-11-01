package com.atguigu.test.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 21:06 2020-10-30
 */
@RestController
@RequestMapping("api/test")
public class TestController {

    @RequestMapping("test")
    public Result<String> test(){
        return Result.ok("1");
    }
}
