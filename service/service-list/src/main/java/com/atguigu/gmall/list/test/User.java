package com.atguigu.gmall.list.test;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * @Author:LiuSir
 * @Date: Create in 18:55 2020-11-11
 *indexName: 索引名
 * type:表名
 * shards,分片：3
 * replicas:复制片
 */
@Data
@Document(indexName = "user",type = "info",shards = 3,replicas = 1)
public class User {
    @Field(type = FieldType.Text,index = true)
    private String name;
    @Field(type = FieldType.Long,index = true)
    private Long age;
    @Field(type = FieldType.Nested,index = true)
    private String[] users;

    @Autowired
   static ElasticsearchRestTemplate elasticsearchRestTemplate;
    public static void main(String[] args) {
        elasticsearchRestTemplate.createIndex(User.class);
        elasticsearchRestTemplate.putMapping(User.class);
    }
}
