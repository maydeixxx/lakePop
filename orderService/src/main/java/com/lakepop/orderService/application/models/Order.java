package com.lakepop.orderService.application.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Order {

    private Long id;

    private LocalDateTime timeStamp;

    private Long productId;

    private BigDecimal amount;

    private String currency;

}
