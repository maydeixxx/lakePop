package com.lakepop.orderService.application.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {

    private Long id;

    private LocalDateTime timeStamp;

    private BigDecimal amount;

    private String currency;

}
