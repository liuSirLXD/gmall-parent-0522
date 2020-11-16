package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 15:05 2020-11-04
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    ListFeignClient listFeignClient;

    //查询商品详情
    @Override
    public Map<String, Object> getItem(Long skuId) {
        //记录该商品在搜索中的热度值
        listFeignClient.hotScore(skuId);
        return getItemByThread(skuId);
    }

    //加入缓存优化
    private Map<String, Object> getItemSignal(Long skuId) {
        long startCurrentTimeMillis = System.currentTimeMillis();
        System.out.println("开始时间：" + startCurrentTimeMillis);
        Map<String, Object> map = new HashMap<>();
        SkuInfo skuInfo = productFeignClient.getSkuById(skuId);

        BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        List<Map<String, Object>> valueMaps = productFeignClient.getSaleAttrValuesBySpu(skuInfo.getSpuId());

        BigDecimal price = productFeignClient.getSkuPrice(skuId);// 直接查数据库
        List<SpuSaleAttr> spuSaleAttrs = productFeignClient.getSpuSaleAttrs(skuInfo.getSpuId(), skuInfo.getId());
        List<SkuImage> skuImages = productFeignClient.getSkuImages(skuId);

        skuInfo.setSkuImageList(skuImages);
        map.put("categoryView", baseCategoryView);
        map.put("skuInfo", skuInfo);
        map.put("price", price);
        map.put("spuSaleAttrList", spuSaleAttrs);

        // 将mybatis的list模式的map 转化成json格式的map
        //[{"valueIds":"20|23","sku_id":16},{"valueIds":"20|25","sku_id":17}]   mybatis中的数据
        //[{"20|23":16},{"20|25":17}]  页面上的数据
        Map<String, Object> valueMap = new HashMap<>();
        for (Map<String, Object> vmap : valueMaps) {
            valueMap.put(vmap.get("valueIds") + "", vmap.get("sku_id"));
        }
        map.put("valuesSkuJson", JSON.toJSONString(valueMap));
        long endCurrentTimeMillis = System.currentTimeMillis();
        System.out.println("结束时间：" + endCurrentTimeMillis);
        System.out.println("总耗时：" + (endCurrentTimeMillis - startCurrentTimeMillis));
        return map;
    }

    //加入多线程优化
    private Map<String, Object> getItemByThread(Long skuId) {
        long startTime = System.currentTimeMillis();
        Map<String,Object> map = new HashMap<>();
        //查询sku基本信息，
        CompletableFuture<SkuInfo> completableFutureSku = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = productFeignClient.getSkuById(skuId);
                List<SkuImage> skuImages = productFeignClient.getSkuImages(skuId);
                skuInfo.setSkuImageList(skuImages);
                map.put("skuInfo",skuInfo);
                return skuInfo;
            }
        },threadPoolExecutor);

        //查询spu的销售属性值，需要依赖skuInfo中的spuId和skuId,
        // sku_sale_attr_value和spu_sale_attr_value内连接查询出同一个spuId下的所有sku的属性值组合值，（需要多加一个IsChecked字段）
        //上面查出的中间表再和spu_sale_attr_value左外连接就可确定商品唯一的sku值
        CompletableFuture<Void> completableFutureSaleAttrs = completableFutureSku.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SpuSaleAttr> spuSaleAttrs = productFeignClient.getSpuSaleAttrs(skuInfo.getSpuId(),skuInfo.getId());//通过spuId查询平台属性
                map.put("spuSaleAttrList",spuSaleAttrs);
            }
        },threadPoolExecutor);

        //切换商品属性的方法，随便在spu的销售属性值上选择，将其确定的
        CompletableFuture<Void> completableFutureValueMaps = completableFutureSku.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<Map<String,Object>> valueMaps = productFeignClient.getSaleAttrValuesBySpu(skuInfo.getSpuId());
                // 将mybatis的list模式的map 转化成json格式的map
                //[{"valueIds":"20|23","sku_id":16},{"valueIds":"20|25","sku_id":17}]   mybatis中的数据
                //[{"20|23":16},{"20|25":17}]  页面上的数据
                Map<String,Object> valueMap = new HashMap<>();
                for (Map<String, Object> temp : valueMaps) {
                    valueMap.put(temp.get("valueIds")+"",temp.get("sku_id"));
                }
                map.put("valuesSkuJson", JSON.toJSONString(valueMap));
            }
        },threadPoolExecutor);

        //查询层级目录显示，通过三级Id可以查询到前面的二级，一级Id
        CompletableFuture<Void> completableFutureCategory = completableFutureSku.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView categoryViews = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                map.put("categoryView",categoryViews);
            }
        },threadPoolExecutor);

        //价钱不放缓存，所以不需要依赖线程，可以只一个独立线程
        CompletableFuture<Void> completableFuturePrice = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal price = productFeignClient.getSkuPrice(skuId);
                map.put("price",price);
            }
        },threadPoolExecutor);

        CompletableFuture.allOf(completableFutureCategory,completableFuturePrice,completableFutureSaleAttrs,completableFutureValueMaps,completableFutureSku).join();
        long endTime = System.currentTimeMillis();
        System.out.println("运行时间:"+(endTime - startTime));
        return map;
    }
}
