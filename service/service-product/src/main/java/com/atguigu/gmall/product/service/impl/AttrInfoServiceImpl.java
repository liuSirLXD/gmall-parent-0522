package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.AttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 12:45 2020-11-01
 */
@Service
public class AttrInfoServiceImpl implements AttrInfoService {

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;



    @Override
    public List<BaseAttrInfo> attrInfoList(String category3Id) {
        QueryWrapper<BaseAttrInfo> wrapper = new QueryWrapper<>();

        wrapper.eq("category_id",category3Id);
        wrapper.eq("category_level",3);

        List<BaseAttrInfo> attrInfoLists = baseAttrInfoMapper.selectList(wrapper);

        for (BaseAttrInfo attrInfoList : attrInfoLists) {
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id",attrInfoList.getId());
            List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(queryWrapper);
            attrInfoList.setAttrValueList(baseAttrValues);
        }
        return attrInfoLists;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if(StringUtils.isEmpty(baseAttrInfo.getId())){
            //添加属性信息
            baseAttrInfoMapper.insert(baseAttrInfo);
        }else{
            //先修改属性信息,先修改也可以保存自己主键Id，下面的删除只是删除属性信息中的的属性值集合
            baseAttrInfoMapper.updateById(baseAttrInfo);
            //修改之前将数据库的老的属性值全部删除，最后将新的属性值添加进数据库
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id",baseAttrInfo.getId());
            baseAttrValueMapper.delete(wrapper);
        }
        //后添加属性值
        List<BaseAttrValue> attrValueLists = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue attrValueList : attrValueLists) {
            attrValueList.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(attrValueList);
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_id",attrId);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(wrapper);
        return baseAttrValues;
    }

}
