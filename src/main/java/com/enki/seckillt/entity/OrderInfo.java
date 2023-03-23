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
public class OrderInfo {
    private Long id;

    private Long userId;

    private Long goodsId;

    private Long addrId;

    private String goodsName;

    private Integer goodsCount;

    private BigDecimal goodsPrice;

    private Integer orderChannel;

    private Integer status;

    private Date createDate;

    private Date payDate;

}