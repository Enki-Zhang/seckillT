package com.enki.seckillt.vo;


import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.entity.OrderInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Enki
 * @Version 1.0
 */
@Setter
@Getter
public class OrderDetailVo {
    private GoodsBo goods;
    private OrderInfo order;
}
