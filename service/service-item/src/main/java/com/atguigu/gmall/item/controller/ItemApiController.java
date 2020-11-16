package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 13:51 2020-11-04
 */
@RestController
@RequestMapping("api/item")
public class ItemApiController {

    @Autowired
    ItemService itemService;

    @RequestMapping("getItem/{skuId}")
    Map<String, Object> getItem(@PathVariable("skuId") Long skuId){
        Map<String,Object> map = itemService.getItem(skuId);
        return map;
    }
}
