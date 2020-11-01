package com.atguigu.gmall.model.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;

import java.util.List;

/**
 * <p>
 * 一级分类表 服务类
 * </p>
 *
 * @author liuxindong
 * @since 2020-10-31
 */
public interface CategoryService  {

    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long category1Id);

    List<BaseCategory3> getCategory3(Long category2Id);
}
