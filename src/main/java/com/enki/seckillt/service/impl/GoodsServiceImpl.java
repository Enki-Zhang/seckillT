package com.enki.seckillt.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.entity.Goods;
import com.enki.seckillt.mapper.GoodsMapper;
import com.enki.seckillt.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Enki
 * @Version 1.0
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsBo> selectAllGoodesList() {
        return goodsMapper.selectAllGoodes();
    }
}
