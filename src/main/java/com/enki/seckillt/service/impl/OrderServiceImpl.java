package com.enki.seckillt.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enki.seckillt.entity.OrderInfo;
import com.enki.seckillt.mapper.OrdeInfoMapper;
import com.enki.seckillt.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Enki
 * @Version 1.0
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrdeInfoMapper, OrderInfo> implements OrderService {
    @Resource
    private OrdeInfoMapper ordeInfoMapper;

    @Override
    public long addOrder(OrderInfo orderInfo) {
        return ordeInfoMapper.insertSelective(orderInfo);
    }

    @Override
    public OrderInfo getOrderInfo(long orderId) {
        return ordeInfoMapper.selectByPrimaryKey(orderId);
    }
}
