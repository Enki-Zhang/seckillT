package com.enki.seckillt.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Enki
 * @Version 1.0
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeckillGoods {
    private Long id;

    private Long goodsId;

    private BigDecimal seckilPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

}