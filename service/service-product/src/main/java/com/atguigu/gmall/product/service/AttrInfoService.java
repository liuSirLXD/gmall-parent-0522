package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 12:45 2020-11-01
 */
public interface AttrInfoService {
    List<BaseAttrInfo> attrInfoList(String category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(Long attrId);
}
