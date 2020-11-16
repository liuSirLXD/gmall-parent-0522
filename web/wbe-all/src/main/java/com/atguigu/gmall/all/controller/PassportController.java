package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @Author:LiuSir
 * @Date: Create in 12:49 2020-11-16
 */
@Controller
public class PassportController {

    //前端登录界面
    @RequestMapping("login.html")
    public String index(String originUrl, Model model){
        //把原始请求地址放入model中，这样携带着地址在请求中了
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
