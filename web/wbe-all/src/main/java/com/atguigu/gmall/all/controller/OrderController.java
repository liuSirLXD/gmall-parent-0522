package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 20:27 2020-11-03
 */
@Controller
public class OrderController {

    @RequestMapping("trade.html")
    public String trade(){
        String userId = "";
        return "order/trade";
    }
}
