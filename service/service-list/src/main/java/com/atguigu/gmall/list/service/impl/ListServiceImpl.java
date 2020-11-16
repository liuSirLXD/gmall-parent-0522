package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.list.repository.GoodsRepository;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.list.test.User;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.client.ProductFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @Author:LiuSir
 * @Date: Create in 21:02 2020-11-11
 */
@Service
public class ListServiceImpl implements ListService {

    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    GoodsRepository goodsRepository;

    @Override
    public void onSale(Long skuId) {
        //如果你的Elastic Search没有Mapping 映射结构你应该调用建Mapping的语句，否则会ES会自动生成默认的结构，
        // 会将你的一些数据本来是keyword类型，默认改成了text类型，后会导致错啊！！！
        createGoods();
        Goods goods = productFeignClient.getGoodsBySkuId(skuId);
        goodsRepository.save(goods);
    }

    @Override
    public void cancelSale(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    @Override
    public void crateUser() {
        elasticsearchRestTemplate.createIndex(User.class);
        elasticsearchRestTemplate.putMapping(User.class);
    }

    @Override
    public void createGoods() {
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
    }

    @Override
    public void hotScore(Long skuId) {
        //查询缓存中的热度值,问题怎么设置热度值的key
        //key ： sku:16:hotScore, +1 和获取值一起
        Long hotScore = (Long) redisTemplate.opsForValue().increment("sku:" + skuId + "hotScore");

        //hotScore达到10的倍数就同步到缓存和ElasticSearch（主要是我们项目太小了10就可以了）
        if(hotScore%10==0){
            //同步到ElasticSearch
            //使用缓存进行缓存稀释请求10次以上，对ElasticSearch更新
            Optional<Goods> byId = goodsRepository.findById(skuId);
            Goods goods = byId.get();
            goodsRepository.save(goods);
        }

    }

    @Override
    public SearchResponseVo list(SearchParam searchParam) {
        SearchResponseVo searchResponseVo = null;
        //dsl语句
        SearchRequest searchRequest = getSearchDSL(searchParam);

        try {
            //查询出这个索引下的所有信息
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //返回结果
            searchResponseVo = parseObject(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponseVo;
    }

    //封装dsl语句
    private SearchRequest getSearchDSL(SearchParam searchParam) {
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String trademark = searchParam.getTrademark();
        String[] props = searchParam.getProps();
        SearchRequest searchRequest = new SearchRequest();//这个类：设置查询指定的某个文档库
        searchRequest.indices("goods");
        searchRequest.types("info");

        //查询资源的构建，kibana中的_source下的是可以查询的数据,查询器
        SearchSourceBuilder searchSourceBuilder= new SearchSourceBuilder();//这个类可以指定分页，查询一些的条件最后作为参数放入searchRequest.source 中
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();//混合构建
        searchSourceBuilder.size(20);
        searchSourceBuilder.from(0);
        //封装dsl语句
        if(null!=category3Id && category3Id > 0 ) {
            TermQueryBuilder queryBuilder = new TermQueryBuilder("category3Id", category3Id);
            boolQueryBuilder.filter(queryBuilder);
        }

        //关键字,还有一个高亮显示
        if(StringUtils.isNotEmpty(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
            //新建一个高亮的显示
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.preTags("<span style='color:red;font-weight:bolder'>");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);

        }

        //商标
        if(StringUtils.isNotEmpty(trademark)){
            Long tmId = Long.parseLong(trademark.split(":")[0]);
            TermQueryBuilder queryBuilder = new TermQueryBuilder("tmId", tmId);
            boolQueryBuilder.filter(queryBuilder);
        }

        //nested查询，属性筛选的操作
        if (null != props && props.length > 0) {
            for (String prop : props) {
                String[] split = prop.split(":");
                Long attrId = Long.parseLong(split[0]);//平台属性id
                String attrValueName = split[1];//平台属性值名称
                String attrName = split[2];//平台属性名

                BoolQueryBuilder boolQueryBuilderForNested= new BoolQueryBuilder();
                TermQueryBuilder queryBuilder = new TermQueryBuilder("attrs.attrId",attrId);
                boolQueryBuilderForNested.filter(queryBuilder);

                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("attrs.attrValue",attrValueName);
                boolQueryBuilderForNested.must(matchQueryBuilder);
                //Es版的嵌套查询
                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs",boolQueryBuilderForNested, ScoreMode.None);

                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }

        //排序
        if(StringUtils.isNotEmpty(searchParam.getOrder())){
            String order = searchParam.getOrder();
            String[] split = order.split(":");
            String type = split[0];//1->hotScore ,2->price
            String sort = split[1];// asc | desc

            //默认排序以热度
            String name = "hotScore";
            if(type.equals("2")){//如果类型时2，则是价格
                name = "price";
            }
            //sort(String name,SortOrder order)
            searchSourceBuilder.sort(name,sort.equals("asc")? SortOrder.ASC : SortOrder.DESC);
        }

        //检索
        searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder);

        //品牌的聚合,terms:聚合的名字
        searchSourceBuilder.aggregation(AggregationBuilders.terms("tmIdAgg").field("tmId")
                                        .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                                                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl")));

        //平台属性值的聚合,nested查询,attrId下有attrsName,attrValue.所以聚合是下面的方式
        searchSourceBuilder.aggregation(AggregationBuilders.nested("attrsAgg","attrs")
                        .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                                .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))));

        System.out.println(searchSourceBuilder.toString());//打印dsl语句
        searchRequest.source(searchSourceBuilder);//检索条件
        return searchRequest;
    }

    //解析返回结果
    private SearchResponseVo parseObject(SearchResponse searchResponse ){

        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //解析命中数
        SearchHits hitList = searchResponse.getHits();
        if(hitList.totalHits > 0) {
            SearchHit[] hits = hitList.getHits();
            List<Goods> goodList = new ArrayList<>();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                Goods goods = JSON.parseObject(sourceAsString, Goods.class);
                //有一个高亮显示，
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(null!=highlightFields && highlightFields.size()>0){
                    HighlightField titleHighlightField = highlightFields.get("title");
                    Text[] highlightFieldFragments = titleHighlightField.getFragments();
                    Text highlightFieldFragment = highlightFieldFragments[0];
                    goods.setTitle(highlightFieldFragment.toString());
                }
                goodList.add(goods);
            }
            searchResponseVo.setGoodsList(goodList);

            //解析商标聚合
            ParsedLongTerms parsedLongTermsTmId = searchResponse.getAggregations().get("tmIdAgg");

            List<SearchResponseTmVo> searchResponseTmVos = parsedLongTermsTmId.getBuckets().stream().map(tmIdBucket -> {
                SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();

                //id
                Long tmIdKey = (Long) tmIdBucket.getKey();
                searchResponseTmVo.setTmId(tmIdKey);

                //name
                ParsedStringTerms parsedStringTermsAttrName = tmIdBucket.getAggregations().get("tmNameAgg");
                List<String> tmNames = parsedStringTermsAttrName.getBuckets().stream().map(tmNameBucket -> {

                    return tmNameBucket.getKeyAsString();
                }).collect(Collectors.toList());
                searchResponseTmVo.setTmName(tmNames.get(0));

                //logoUrl
                ParsedStringTerms TmLogoUrlParsedStringTerms = tmIdBucket.getAggregations().get("tmLogoUrlAgg");
                List<String> tmLogoUrls = TmLogoUrlParsedStringTerms.getBuckets().stream().map(tmLogoUrlBucket -> {
                    return tmLogoUrlBucket.getKeyAsString();
                }).collect(Collectors.toList());
                searchResponseTmVo.setTmLogoUrl(tmLogoUrls.get(0));

                return searchResponseTmVo;
            }).collect(Collectors.toList());
            searchResponseVo.setTrademarkList(searchResponseTmVos);

            //解析属性的聚合
            ParsedNested attrsAggParseNested= searchResponse.getAggregations().get("attrsAgg");

            ParsedLongTerms attIdParsedLongTerms = attrsAggParseNested.getAggregations().get("attrIdAgg");
            List<SearchResponseAttrVo> searchResponseAttrVos= attIdParsedLongTerms.getBuckets().stream().map(attrIdBucket -> {
                //把聚合的添加的进入searchResponseAttrVo中
                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                //attrId 商品Id
                Long attrsId = (Long) attrIdBucket.getKey();
                searchResponseAttrVo.setAttrId(attrsId);

                //attrName，name只有一个
                ParsedStringTerms attrNameParsedStringTerms = attrIdBucket.getAggregations().get("attrNameAgg");
                String attrNames = attrNameParsedStringTerms.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrNames);

                //attrValue，他是集合
                ParsedStringTerms attrValueParsedStringTerms = attrIdBucket.getAggregations().get("attrValueAgg");
                List<String> attrValues = attrValueParsedStringTerms.getBuckets().stream().map( attrValueBucket-> {
                    return attrValueBucket.getKeyAsString();
                }).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValueList(attrValues);

                return searchResponseAttrVo;
            }).collect(Collectors.toList());
            searchResponseVo.setAttrsList(searchResponseAttrVos);

        }
        return searchResponseVo;
    }
}
