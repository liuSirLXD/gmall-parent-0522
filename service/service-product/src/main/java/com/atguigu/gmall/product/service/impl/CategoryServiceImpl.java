package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 一级分类表 服务实现类
 * </p>
 *
 * @author liuxindong
 * @since 2020-10-31
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;

    //查询一级分类
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    //通过一级分类的id查询二级分类
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category1_id",category1Id);
        List<BaseCategory2> category2List = baseCategory2Mapper.selectList(queryWrapper);
        return category2List;
    }

    //通过一级分类的id查询二级分类
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        wrapper.eq("category2_id",category2Id);
        List<BaseCategory3> category3List = baseCategory3Mapper.selectList(wrapper);
        return category3List;
    }

    @GmallCache(prefix = "GmallCache:Category")
    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        QueryWrapper<BaseCategoryView> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(wrapper);
        return baseCategoryView;
    }

    @Override
    public List<JSONObject> categoryList() {
        //将这个分类数据全部查询出来
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);

        //将这些数据封装为json数据
        List<JSONObject> category1List = new ArrayList<>();
        //怎么把二级分类和三级分类也封装到同一个JSONObject,因为在前端其分层的一级分类，二级分类和三级分类都是categoryId
        //按照一级分类Id分组
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        for (Map.Entry<Long, List<BaseCategoryView>> category1group : category1Map.entrySet()) {
            //一级分类的Id和名称
            Long category1Id = category1group.getKey();
            String category1Name = category1group.getValue().get(0).getCategory1Name();
            //封装一级分类的数据
            JSONObject category1jsonObject = new JSONObject();
            category1jsonObject.put("categoryId",category1Id);
            category1jsonObject.put("categoryName",category1Name);

            //封装二级分类
            //将这些数据封装为json数据
            List<JSONObject> category2List = new ArrayList<>();

            //按照2级分类Id分组
            Map<Long, List<BaseCategoryView>> category2Map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> category2group : category2Map.entrySet()) {
                //2级分类的Id和名称
                Long category2Id = category2group.getKey();
                String category2Name = category2group.getValue().get(0).getCategory2Name();
                //封装2级分类的数据
                JSONObject category2jsonObject = new JSONObject();
                category2jsonObject.put("categoryId", category2Id);
                category2jsonObject.put("categoryName", category2Name);

                //将这些数据封装为json数据
                List<JSONObject> category3List = new ArrayList<>();

                //按照3级分类Id分组
                Map<Long, List<BaseCategoryView>> category3Map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for (Map.Entry<Long, List<BaseCategoryView>> category3group : category3Map.entrySet()) {
                    //3级分类的Id和名称
                    Long category3Id = category3group.getKey();
                    String category3Name = category3group.getValue().get(0).getCategory3Name();
                    //封装3级分类的数据
                    JSONObject category3jsonObject = new JSONObject();
                    category3jsonObject.put("categoryId", category3Id);
                    category3jsonObject.put("categoryName", category3Name);
                    category3List.add(category3jsonObject);
                }
                //2级
                category2jsonObject.put("categoryChild",category3List);
                category2List.add(category2jsonObject);
            }
            //
            category1jsonObject.put("categoryChild",category2List);
            category1List.add(category1jsonObject);
        }
        return category1List;
    }
}
