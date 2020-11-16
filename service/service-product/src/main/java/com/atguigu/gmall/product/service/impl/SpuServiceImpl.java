package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 17:40 2020-11-01
 */
@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    SpuInfoMapper spuInfoMapper;
    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    SpuImageMapper spuImageMapper;
    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public IPage<SpuInfo> spuList(IPage<SpuInfo> spuInfoIPage, Long category3Id) {
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        IPage<SpuInfo> selectPage = spuInfoMapper.selectPage(spuInfoIPage, wrapper);
        return selectPage;
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存主表
        spuInfoMapper.insert(spuInfo);
        //得到主键
        Long spuId = spuInfo.getId();
        //保存图片表
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            //这是spu_image表的外键
            spuImage.setSpuId(spuId);
            //保存图片表
            spuImageMapper.insert(spuImage);
        }

        //保存销售属性表
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            //保存销售属性表
            spuSaleAttr.setSpuId(spuId);
            spuSaleAttrMapper.insert(spuSaleAttr);

            //保存销售属性值表
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(spuId);//spuId+saleAttrId联合外键
                //前端设计问题，我们补上没有显示的值
                spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getBaseSaleAttrId());
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }
        }
    }

    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        QueryWrapper<SpuSaleAttr> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectList(wrapper);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {
            QueryWrapper<SpuSaleAttrValue> wrapper1 = new QueryWrapper<>();
            wrapper.eq("spu_id",spuId);
            wrapper.eq("base_sale_attr_id",spuSaleAttr.getBaseSaleAttrId());//联合组件
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(wrapper1);
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }
        return spuSaleAttrs;
    }


    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuImage> spuImages = spuImageMapper.selectList(wrapper);
        return spuImages;
    }

    @GmallCache(prefix = "GmallCache:SpuSaleA")
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrs(Long spu_id,Long sku_id) {

        // 实现一个三个表(spu_sale_attr,spu_sale_attr_value,sku_sale_attr_value)联合查询
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(spu_id,sku_id);

        return spuSaleAttrs;
    }


}
