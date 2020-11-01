package com.atguigu.gmall.model.product.service;

import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 17:39 2020-11-01
 */
public interface SpuService {
    IPage<SpuInfo> spuList(IPage<SpuInfo> spuInfoIPage, Long category3Id);

    List<BaseSaleAttr> baseSaleAttrList();
}
