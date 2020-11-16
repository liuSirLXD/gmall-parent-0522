package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 15:05 2020-11-04
 */
public interface ItemService {
    Map<String, Object> getItem(Long skuId);
}
