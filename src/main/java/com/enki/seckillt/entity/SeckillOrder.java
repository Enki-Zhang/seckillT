package com.enki.seckillt.entity;

import lombok.*;

/**
 * @author Enki
 * @Version 1.0
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder {
    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;


}