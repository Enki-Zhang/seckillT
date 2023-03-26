package com.enki.seckillt.service.impl;


import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.common.Const;
import com.enki.seckillt.entity.OrderInfo;
import com.enki.seckillt.entity.SeckillOrder;
import com.enki.seckillt.entity.User;
import com.enki.seckillt.mapper.SeckillOrderMapper;
import com.enki.seckillt.redis.RedisID;
import com.enki.seckillt.redis.RedisService;
import com.enki.seckillt.redis.SeckillKey;
import com.enki.seckillt.service.OrderService;
import com.enki.seckillt.service.SeckillGoodsService;
import com.enki.seckillt.service.SeckillOrderService;
import com.enki.seckillt.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Enki
 * @Version 1.0
 */
@Slf4j
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {

    @Autowired
    SeckillOrderMapper seckillOrderMapper;

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    RedisService redisService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    OrderService orderService;

    @Override
    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodsId) {
       return query().eq("user_id", userId).eq("goods_id", goodsId).one();
//        return seckillOrderMapper.selectByUserIdAndGoodsId(userId, goodsId);
    }

    @Transactional
    @Override
    public OrderInfo insert(User user, GoodsBo goods) {
        //秒杀商品库存减一
        int success = seckillGoodsService.reduceStock(goods.getId());
        if (success == 1) {
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setCreateDate(new Date());
            orderInfo.setAddrId(0L);
            orderInfo.setGoodsCount(1);
            orderInfo.setGoodsId(goods.getId());
            orderInfo.setGoodsName(goods.getGoodsName());
            orderInfo.setGoodsPrice(goods.getSeckillPrice());
            orderInfo.setOrderChannel(1);
            orderInfo.setStatus(0);
            orderInfo.setUserId((long) user.getId());
            //添加信息进订单
            long orderId = orderService.addOrder(orderInfo);
            RedisID redisID = new RedisID(stringRedisTemplate);
            long seckillID = redisID.nextId("orderId");
            log.info("orderId -->" + orderId + "");
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(seckillID);
            seckillOrder.setGoodsId(goods.getId());
            seckillOrder.setOrderId(orderInfo.getId());
            seckillOrder.setUserId((long) user.getId());
            //插入秒杀表
            seckillOrderMapper.insertSelective(seckillOrder);
            return orderInfo;
        } else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    @Override
    public OrderInfo getOrderInfo(long orderId) {
//        根据订单信息 查询订单
        SeckillOrder seckillOrder  = query().eq("order_id", orderId).one();
        if (seckillOrder == null) {
            return null;
        }
        return orderService.getOrderInfo(seckillOrder.getOrderId());
    }

    /**
     * 秒杀成功
     *
     * @param userId
     * @param goodsId
     * @return
     */
    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder order = query().eq("user_id", userId).eq("goods_id", goodsId).one();
//        SeckillOrder order = getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {//秒杀成功
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 验证路径
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(User user, long goodsId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getSeckillPath, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    /**
     * 创建秒杀路径
     *
     * @param user
     * @param goodsId
     * @return
     */
    public String createMiaoshaPath(User user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
//        加密路径
//        全局唯一
        RedisID redisID = new RedisID(stringRedisTemplate);
        String path = redisID.nextId(SeckillKey.getSeckillPath.getPrefix())+ "";
        log.info("秒杀路径{}",path);
//        String str = MD5Util.md5(UUID.randomUUID() + "123456");
        redisService.set(SeckillKey.getSeckillPath, "" + user.getId() + "_" + goodsId, path, Const.RedisCacheExtime.GOODS_ID);
        return path;
    }


    /*
     * 秒杀商品结束标记
     * */
    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, "" + goodsId, true, Const.RedisCacheExtime.GOODS_ID);
    }

    /*
     * 查看秒杀商品是否已经结束
     * */
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, "" + goodsId);
    }

}
