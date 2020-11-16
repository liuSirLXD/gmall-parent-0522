package com.atguigu.gmall.model.product.client;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 14:51 2020-11-04
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {

    @RequestMapping("api/product/getSkuById/{skuId}")
    SkuInfo getSkuById(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/product/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable("skuId")Long skuId);

    @RequestMapping("api/product/getSpuSaleAttrs/{spu_id}/{sku_id}")
    List<SpuSaleAttr> getSpuSaleAttrs(@PathVariable("spu_id") Long spu_id,@PathVariable("sku_id")Long sku_id);

    @RequestMapping("api/product/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id")Long category3Id);

    @RequestMapping("api/product/getSkuImages/{skuId}")
    List<SkuImage> getSkuImages(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/product/getSaleAttrValuesBySpu/{spuId}")
    List<Map<String, Object>> getSaleAttrValuesBySpu(@PathVariable("spuId") Long spuId);

    @RequestMapping("api/product/getGoodsBySkuId/{skuId}")
    Goods getGoodsBySkuId(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/product/categoryList")
    List<JSONObject> categoryList();

}
