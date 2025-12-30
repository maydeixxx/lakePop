package com.lakepop.orderService.api.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {

    private Long id;

    private LocalDateTime timeStamp;

    private Long productId;

    private BigDecimal amount;

    private String currency;

}
