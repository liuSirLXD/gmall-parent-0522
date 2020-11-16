package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 12:49 2020-11-01
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    List<SearchAttr> selectBaseAttrsBySkuId(@Param("skuId")Long skuId);
}
