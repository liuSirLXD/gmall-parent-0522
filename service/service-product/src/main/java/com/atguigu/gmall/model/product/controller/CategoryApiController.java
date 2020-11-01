package com.atguigu.gmall.model.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 一级分类表 前端控制器
 * </p>
 *
 * @author liuxindong
 * @since 2020-10-31
 */
@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class CategoryApiController {

    @Autowired
    CategoryService categoryService;

    @RequestMapping("getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1List = categoryService.getCategory1();
        return Result.ok(category1List);
    }

    @RequestMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id){
        List<BaseCategory2> category2List = categoryService.getCategory2(category1Id);
        return Result.ok(category2List);
    }

    @RequestMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id){
        List<BaseCategory3> category3List = categoryService.getCategory3(category2Id);
        return Result.ok(category3List);
    }

}

