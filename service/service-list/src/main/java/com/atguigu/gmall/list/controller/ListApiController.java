package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.list.test.User;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:LiuSir
 * @Date: Create in 13:56 2020-11-11
 */
@RestController
@RequestMapping("api/list")
@CrossOrigin
public class ListApiController {
    @Autowired
    ListService listService;

    @RequestMapping("hotScore/{skuId}")
    Result hotScore(@PathVariable("skuId") Long skuId){
        listService.hotScore(skuId);
        return Result.ok();
    }

    @RequestMapping("onSale/{skuId}")
    Result onSale(@PathVariable("skuId") Long skuId){
        //根据skuId查询goods
        listService.onSale(skuId);
        return Result.ok();
    }

    @RequestMapping("cancelSale/{skuId}")
    Result cancelSale(@PathVariable("skuId") Long skuId){
        //根据skuId删除
        listService.cancelSale(skuId);
        return Result.ok();
    }

    @RequestMapping("createUser")
    public Result crateUser(){
        listService.crateUser();
        return Result.ok();
    }

    @RequestMapping("createGoods")
    public Result createGoods(){
       listService.createGoods();
        return Result.ok();
    }

    @RequestMapping("list")
    SearchResponseVo list(@RequestBody SearchParam searchParam){
        SearchResponseVo searchResponseVo =  listService.list(searchParam);
        return searchResponseVo;
    }

}
