package com.atguigu.gmall.model.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.model.product.service.TradeMarkService;
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
 * @Description:
 * @Date: Create in 20:33 2020-11-01
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class TradeMarkController {
    @Autowired
    TradeMarkService tradeMarkService;

    //得到品牌列表
    @RequestMapping("baseTrademark/{pageNum}/{pageSize}")
    public Result baseTrademark(@PathVariable("pageNum")Long pageNum, @PathVariable("pageSize")Long pageSize){
        IPage<BaseTrademark> tradeMarkIPage = new Page<>();
        tradeMarkIPage.setSize(pageSize);
        tradeMarkIPage.setPages(pageNum);
        IPage<BaseTrademark> trademarks =  tradeMarkService.baseTrademark(tradeMarkIPage);
        return Result.ok(trademarks);
    }
    @RequestMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> trademarks = tradeMarkService.getTrademarkList();
        return Result.ok(trademarks);
    }
}
