package com.atguigu.gmall.list.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:LiuSir
 * @Date: Create in 21:08 2020-11-11
 */

public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
