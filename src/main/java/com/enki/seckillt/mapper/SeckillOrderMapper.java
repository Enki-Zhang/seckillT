package com.enki.seckillt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enki.seckillt.entity.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Enki
 * @Version 1.0
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
    int deleteByPrimaryKey(Long id);

    int insert(SeckillOrder record);

    int insertSelective(SeckillOrder record);

    SeckillOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SeckillOrder record);

    int updateByPrimaryKey(SeckillOrder record);

    SeckillOrder selectByUserIdAndGoodsId(@Param("userId") long userId , @Param("goodsId") long goodsId );
}