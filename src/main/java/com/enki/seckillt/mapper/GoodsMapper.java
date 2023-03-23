package com.enki.seckillt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Enki
 * @Version 1.0
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    int deleteByPrimaryKey(Long id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKeyWithBLOBs(Goods record);

    int updateByPrimaryKey(Goods record);

    List<GoodsBo> selectAllGoodes();

    GoodsBo getseckillGoodsBoByGoodsId(long goodsId);

    int updateStock(long goodsId);
}