package com.atguigu.gmall.model.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.service.SpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description: spu 操作
 * @Date: Create in 17:30 2020-11-01
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SpuController {
    @Autowired
    SpuService spuService;

    //获取分页属性
    @RequestMapping("{pageNum}/{pageSize}")
    public Result spuList(Long category3Id, @PathVariable("pageNum")Long pageNum,@PathVariable("pageSize")Long pageSize){
        IPage<SpuInfo> spuInfoIPage = new Page<>();
        spuInfoIPage.setSize(pageSize);
        spuInfoIPage.setPages(pageNum);

        IPage<SpuInfo> spuInfoIPages = spuService.spuList(spuInfoIPage,category3Id);
        return Result.ok(spuInfoIPages);
    }
    //获取销售属性
    @RequestMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> saleAttrLists = spuService.baseSaleAttrList();
        return Result.ok(saleAttrLists);
    }
}
