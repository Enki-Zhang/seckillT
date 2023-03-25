package com.enki.seckillt.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.entity.SeckillGoods;
import com.enki.seckillt.mapper.GoodsMapper;
import com.enki.seckillt.mapper.SeckillGoodsMapper;
import com.enki.seckillt.service.SeckillGoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Enki
 * @Version 1.0
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService {
    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 获取秒杀商品清单
     *
     * @return
     */
    @Override
    public List<GoodsBo> getSeckillGoodsList() {
        return goodsMapper.selectAllGoodes();
    }

    @Override
    public GoodsBo getseckillGoodsBoByGoodsId(long goodsId) {
        return goodsMapper.getseckillGoodsBoByGoodsId(goodsId);
    }

    @Override
    public int reduceStock(long goodsId) {
        return goodsMapper.updateStock(goodsId);
    }
}
