package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;

import java.util.List;

/**
 * <p>
 * 层级分类 服务类
 * </p>
 *
 * @author liuxindong
 * @since 2020-10-31
 */
public interface CategoryService  {

    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long category1Id);

    List<BaseCategory3> getCategory3(Long category2Id);

    BaseCategoryView getCategoryView(Long category3Id);

    List<JSONObject> categoryList();
}
