package com.lakepop.orderService.api.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDTO {

    private Long id;

    private LocalDateTime timeStamp;

    private Long productId;

    private BigDecimal amount;

    private String currency;

}
