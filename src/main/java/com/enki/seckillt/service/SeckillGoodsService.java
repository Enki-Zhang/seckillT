package com.enki.seckillt.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.entity.SeckillGoods;

import java.util.List;

/**
 * @author Enki
 * @Version 1.0
 */
public interface SeckillGoodsService extends IService<SeckillGoods> {

    List<GoodsBo> getSeckillGoodsList();


    GoodsBo getseckillGoodsBoByGoodsId(long goodsId);

    int reduceStock(long goodsId);
}
