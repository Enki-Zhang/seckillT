package com.enki.seckillt.vo;


import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Enki
 * @Version 1.0
 */
@Getter
@Setter
public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsBo goods ;
    private User user;
}
