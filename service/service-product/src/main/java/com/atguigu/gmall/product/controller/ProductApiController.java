package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.AttrInfoService;
import com.atguigu.gmall.product.service.CategoryService;
import com.atguigu.gmall.product.service.SkuService;
import com.atguigu.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 不能让外部直接访问service的核心代码，所以出现这个统一对外访问的接口controller
 * 因为外部不能直接访问内部接口
 * @Author:LiuSir
 * @Date: Create in 13:43 2020-11-04
 */
@RestController
@RequestMapping("api/product")
@CrossOrigin
public class ProductApiController {
    @Autowired
    SkuService skuService;
    @Autowired
    SpuService spuService;
    @Autowired
    AttrInfoService attrService;
    @Autowired
    CategoryService categoryService;


    //查询商品的skuInfo 表
    @RequestMapping("getSkuById/{skuId}")
    SkuInfo getSkuById(@PathVariable("skuId") Long skuId){
        SkuInfo skuById = skuService.getSkuById(skuId);
        return skuById;
}

    //查询商品的价格，不能放缓存，缓存数据的一致性不好
    @RequestMapping("getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable("skuId")Long skuId){
        return skuService.getSkuPrice(skuId);
    }

    //查询商品销售属性
    @RequestMapping("getSpuSaleAttrs/{spu_id}/{sku_id}")
    List<SpuSaleAttr> getSpuSaleAttrs(@PathVariable("spu_id") Long spu_id,@PathVariable("sku_id")Long sku_id){
        return spuService.getSpuSaleAttrs(spu_id,sku_id);
    }

    //商品层级目录，面包屑
    @RequestMapping("getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id")Long category3Id){
        return categoryService.getCategoryView(category3Id);
    }

    //获取图片信息
    @RequestMapping("getSkuImages/{skuId}")
    List<SkuImage> getSkuImages(@PathVariable("skuId") Long skuId){
        return skuService.getSkuImages(skuId);
    }

    //通过spuId查询具体的sku达到切换属性值
    @RequestMapping("getSaleAttrValuesBySpu/{spuId}")
    List<Map<String, Object>> getSaleAttrValuesBySpu(@PathVariable("spuId") Long spuId){
        return skuService.getSaleAttrValuesBySpu(spuId);
    }

    @RequestMapping("getGoodsBySkuId/{skuId}")
    Goods getGoodsBySkuId(@PathVariable("skuId") Long skuId){
        Goods goods = skuService.getGoodsBySkuId(skuId);
        return goods;
    }

    @RequestMapping("categoryList")
    List<JSONObject> categoryList(){
        List<JSONObject> jsonObjects = categoryService.categoryList();
        return jsonObjects;
    }
}
