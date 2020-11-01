package com.atguigu.gmall.model.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.service.AttrInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 10:24 2020-11-01
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class AttrApiController {
    @Autowired
    AttrInfoService attrInfoService;

    //根据分类查出分类信息，
    @RequestMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id
            ,@PathVariable Long category2Id
            ,@PathVariable String category3Id){
        List<BaseAttrInfo> attrInfoList = attrInfoService.attrInfoList(category3Id);
        return Result.ok(attrInfoList);
    }

    //添加平台属性
    @RequestMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        attrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok(null);
    }
    //查询
    @RequestMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId){
        List<BaseAttrValue>  baseAttrValue= attrInfoService.getAttrValueList(attrId);
        return Result.ok(baseAttrValue);
    }

}
