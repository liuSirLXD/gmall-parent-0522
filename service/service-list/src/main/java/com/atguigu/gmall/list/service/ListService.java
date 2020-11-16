package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

/**
 * @Author:LiuSir
 * @Date: Create in 21:02 2020-11-11
 */
public interface ListService {
    void onSale(Long skuId);

    void cancelSale(Long skuId);

    void crateUser();

    void createGoods();

    void hotScore(Long skuId);

    SearchResponseVo list(SearchParam searchParam);
}
