package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;


/**
  Create in 20:27 2020-11-03
 */
@Controller
public class ItemController {

    @Autowired
    ItemFeignClient itemFeignClient;

    @RequestMapping("{skuId}.html")
    public String index(@PathVariable("skuId") Long skuId,Model model){
        // 调用item的接口
        Map<String,Object> map = new HashMap<>();
        map = itemFeignClient.getItem(skuId);
        model.addAllAttributes(map);
        return "item/index";
    }
}
