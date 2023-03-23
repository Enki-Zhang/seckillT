package com.enki.seckillt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enki.seckillt.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Enki
 * @Version 1.0
 */
@Mapper
public interface OrdeInfoMapper extends BaseMapper<OrderInfo> {
    int deleteByPrimaryKey(Long id);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);
}