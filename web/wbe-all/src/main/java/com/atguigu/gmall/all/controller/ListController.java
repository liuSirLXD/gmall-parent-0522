package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.model.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @Author:LiuSir
 * @Date: Create in 15:36 2020-11-11
 */
@Controller//渲染页面的
public class ListController {

    @Autowired
    ListFeignClient listFeignClient;
    @Autowired
    ProductFeignClient productFeignClient;

    @RequestMapping("index.html")
    public String index(Model model){
        List<JSONObject> categoryList = new ArrayList<>();
        categoryList = productFeignClient.categoryList();
        model.addAttribute("list",categoryList);
        return "index";
    }

    @RequestMapping({"list.html","search.html"})
    public String list(Model model,SearchParam searchParam){

        //查询出来的所有数据封装进SearchResponseVo
        SearchResponseVo searchResponseVo = listFeignClient.list(searchParam);
        //将查询的数据返回个前端，一般前端都是josn格式
        model.addAttribute("goodsList",searchResponseVo.getGoodsList());//检索出来的商品信息
        model.addAttribute("attrsList",searchResponseVo.getAttrsList());//所有商品的顶头显示的筛选属性和属性值
        model.addAttribute("trademarkList",searchResponseVo.getTrademarkList());//品牌
        model.addAttribute("urlParam",getUrlParam(searchParam));

        //面包屑
        List<SearchAttr> searchAttrs = new ArrayList<>();//将面包屑的值封装成一个集合，可能有多个面包屑
        if(null!=searchParam.getProps() && searchParam.getProps().length>0){
            //这是点击属性面包屑的值
            String[] propsParamList = searchParam.getProps();
            for (String temp : propsParamList) {
                String[] split = temp.split(":");

                Long attrId = Long.parseLong(split[0]);//平台属性id
                String attrValueName = split[1];//平台属性值名称
                String attrName = split[2];//平台属性名

                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(attrId);
                searchAttr.setAttrName(attrName);
                searchAttr.setAttrValue(attrValueName);

                searchAttrs.add(searchAttr);
            }
            model.addAttribute("propsParamList",searchAttrs);
        }

        //商标的面包屑
        if(!StringUtils.isEmpty(searchParam.getTrademark())){
            //trademark=2:华为,所以切割后的第二位是商标
            model.addAttribute("trademarkParam",searchParam.getTrademark().split(":")[1]);
        }

        //排序
        if(!StringUtils.isEmpty(searchParam.getOrder())){
            String order = searchParam.getOrder();

            String[] split = order.split(":");
            String type = split[0];//1->hotScore ,2->price
            String sort = split[1];// asc | desc

            Map<String,String> orderMap = new HashMap<>();
            orderMap.put("type",type);
            orderMap.put("sort",sort);

            model.addAttribute("orderMap",orderMap);
        }

        return "list/index";
    }

    //拼接属性筛选的参数
    private String getUrlParam(SearchParam searchParam) {
        StringBuffer urlParam = new StringBuffer("list.html?");

        Long category3Id = searchParam.getCategory3Id();
        String trademark = searchParam.getTrademark();
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();

        if(null!=category3Id && category3Id > 0){
            urlParam.append("&category3Id="+category3Id);
        }

        if(!StringUtils.isEmpty(trademark)){
            urlParam.append("&trademark="+trademark);
        }

        if(!StringUtils.isEmpty(keyword)){
            urlParam.append("keyword="+keyword);
        }

        if(null!=props && props.length>0){//null在前面可以避免空指针异常，如果props.length在前面每次刷新页面都会报空指针
            for (String prop : props) {
                urlParam.append("&props="+prop);
            }
        }

        return urlParam.toString();
    }
}
