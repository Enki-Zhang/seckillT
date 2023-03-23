package com.enki.seckillt.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.entity.Goods;

import java.util.List;

/**
 * @author Enki
 * @Version 1.0
 */
public interface GoodsService extends IService<Goods> {
    List<GoodsBo> selectAllGoodesList();
}
