package com.enki.seckillt.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.enki.seckillt.entity.OrderInfo;

/**
 * @author Enki
 * @Version 1.0
 */
public interface OrderService extends IService<OrderInfo> {

    long addOrder(OrderInfo orderInfo);

    OrderInfo getOrderInfo(long rderId);
}
