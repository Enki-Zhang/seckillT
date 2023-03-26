package com.enki.seckillt.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.entity.OrderInfo;
import com.enki.seckillt.entity.SeckillOrder;
import com.enki.seckillt.entity.User;

/**
 * @author Enki
 * @Version 1.0
 */
public interface SeckillOrderService extends IService<SeckillOrder> {

    SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodsId);

    OrderInfo insert(User user, GoodsBo goodsBo);

    OrderInfo getOrderInfo(long orderId);

    long getSeckillResult(Long userId, long goodsId);

    boolean checkPath(User user, long goodsId, String path);

    String createMiaoshaPath(User user, long goodsId);

}
