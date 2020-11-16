package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
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

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    List<SpuImage> spuImageList(Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrs(Long spu_id,Long sku_id);
}
