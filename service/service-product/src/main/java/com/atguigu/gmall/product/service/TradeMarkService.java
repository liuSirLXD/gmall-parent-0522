package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 21:02 2020-11-01
 */
public interface TradeMarkService {
    List<BaseTrademark> getTrademarkList();

    IPage<BaseTrademark> baseTrademark(IPage<BaseTrademark> tradeMarkIPage);
}
