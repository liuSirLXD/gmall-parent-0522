package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.TradeMarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 21:03 2020-11-01
 */
@Service
public class TradeMarkServiceImpl implements TradeMarkService {
    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    @Override
    public IPage<BaseTrademark> baseTrademark(IPage<BaseTrademark> tradeMarkIPage) {
        return baseTrademarkMapper.selectPage(tradeMarkIPage,null);
    }
}
