package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 11:19 2020-11-03
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SkuApiController {
    @Autowired
    SkuService skuService;

    //保存sku信息
    @RequestMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    //查询sku列表
    @RequestMapping("list/{page}/{size}")
    public Result list(@PathVariable Long page,@PathVariable Long size){
        IPage<SkuInfo> skuInfoIPage = new Page<>();
        skuInfoIPage.setCurrent(page);
        skuInfoIPage.setSize(size);
        IPage<SkuInfo> skuInfoList = skuService.skuList(skuInfoIPage);
        return Result.ok(skuInfoList);
    }

    //上架商品
    @RequestMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        skuService.onSale(skuId);
        return Result.ok();
    }

    //下架商品cancelSale/{skuId}
    @RequestMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        skuService.cancelSale(skuId);
        return Result.ok();
    }
}
