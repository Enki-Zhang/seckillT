package com.enki.seckillt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enki.seckillt.entity.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Enki
 * @Version 1.0
 */
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {
    int deleteByPrimaryKey(Long id);

    int insert(SeckillGoods record);

    int insertSelective(SeckillGoods record);

    SeckillGoods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SeckillGoods record);

    int updateByPrimaryKey(SeckillGoods record);
}