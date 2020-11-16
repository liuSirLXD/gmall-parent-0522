package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 14:05 2020-11-03
 */
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;
    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;
    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ListFeignClient listFeignClient;



    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //保存sku信息表
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();
        //保存skuImage表
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }
        //保存skuAttrValue 表
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }
        //保存skuSaleAttrValue表
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }
    }

    @Override
    public IPage<SkuInfo> skuList(IPage<SkuInfo> skuInfoIPage) {
        IPage<SkuInfo> skuInfoList = skuInfoMapper.selectPage(skuInfoIPage,null);
        return skuInfoList;
    }

    @Override
    public void onSale(Long skuId) {
        //上架
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
        //同步引擎搜索
        listFeignClient.onSale(skuId);
    }

    @Override
    public void cancelSale(Long skuId) {
        //下架
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo) ;
        //同步引擎搜索
        listFeignClient.cancelSale(skuId);
    }

    @GmallCache(prefix = RedisConst.SKUKEY_PREFIX+"images:")
    @Override
    public List<SkuImage> getSkuImages(Long skuId) {
        QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(wrapper);
        return skuImages;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {
       QueryWrapper<SkuInfo> wrapper = new QueryWrapper<>();
       wrapper.eq("id",skuId);
        SkuInfo skuInfo = skuInfoMapper.selectOne(wrapper);
        return skuInfo.getPrice();
    }

    //为提高高并发时查询效率，我们加入redis
    @GmallCache(prefix = "GmallCache:SkuInfo")
    @Override
    public SkuInfo getSkuById(Long skuId) {
        SkuInfo skuInfo = getSkuInfoFromDB(skuId);
        return skuInfo;
    }

    //加缓存的代码
    private SkuInfo redisBySkuId(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        //先查询缓存
        skuInfo = (SkuInfo) redisTemplate.opsForValue().get("sku:" + skuId + ":info");
        //如果缓存没有，访问DB
        if(null==skuInfo){
            String lock = UUID.randomUUID().toString();
            Boolean ifDB = redisTemplate.opsForValue().setIfAbsent("sku:" + skuId + ":lock",lock,30, TimeUnit.SECONDS);
            if(ifDB) {
                //访问DB
                skuInfo = getSkuInfoFromDB(skuId);
                if (null != skuInfo) {
                    //把查询出来的数据，放入缓存中，同步缓存
                    redisTemplate.opsForValue().set("sku:" + skuId + ":info",skuInfo);
                    //解锁
//                    SkuInfo delLock = (SkuInfo) redisTemplate.opsForValue().get("sku:" + skuId + ":lock");
//                    if(delLock.equals(lock)){
//                        //删除锁，解锁操作
//                        redisTemplate.delete("sku:" + skuId + ":info");
//                    }
                    //使用lua脚本，将删除和解锁一起操作，避免要删除锁时，redis里面锁刚好过期的情况
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    // 设置lua脚本返回的数据类型
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    // 设置lua脚本返回类型为Long
                    redisScript.setResultType(Long.class);
                    redisScript.setScriptText(script);
                    redisTemplate.execute(redisScript, Arrays.asList("sku:" + skuId + ":lock"),lock);
                    System.out.println("");
                }
            }else {
                //自旋
                return getSkuById(skuId);
            }
        }
        return skuInfo;
    }

    //访问DB
    private SkuInfo getSkuInfoFromDB(Long skuId) {
        QueryWrapper<SkuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",skuId);
        return skuInfoMapper.selectOne(wrapper);
    }

    @GmallCache(prefix = "GmallCache:SaleValue")
    @Override
    public List<Map<String, Object>> getSaleAttrValuesBySpu(Long spuId) {
        List<Map<String, Object>> maps = skuSaleAttrValueMapper.getSaleAttrValuesBySpu(spuId);
        return maps;
    }

    @Override
    public Goods getGoodsBySkuId(Long skuId) {
        Goods goods = new Goods();

        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(skuInfo.getTmId());
        QueryWrapper<BaseCategoryView> baseCategoryViewWrapper = new QueryWrapper<>();
        baseCategoryViewWrapper.eq("category3_id",skuInfo.getCategory3Id());
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(baseCategoryViewWrapper);
        goods.setTitle(skuInfo.getSkuName());
        goods.setId(skuId);
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        goods.setTmId(skuInfo.getTmId());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setCreateTime(new Date());
        goods.setCategory3Name(baseCategoryView.getCategory3Name());
        goods.setCategory3Id(baseCategoryView.getCategory3Id());
        goods.setCategory2Name(baseCategoryView.getCategory2Name());
        goods.setCategory2Id(baseCategoryView.getCategory2Id());
        goods.setCategory1Name(baseCategoryView.getCategory1Name());
        goods.setCategory1Id(baseCategoryView.getCategory1Id());

        //需要的数据就差这个数据了，查询的平台属性的集合
        List<SearchAttr> searchAttrs = new ArrayList<>();
        searchAttrs = baseAttrInfoMapper.selectBaseAttrsBySkuId(skuId);
        goods.setAttrs(searchAttrs);
        return goods;
    }
}
