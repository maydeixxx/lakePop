package com.lakepop.orderService.application.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Order {

    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String payUrl;

    private LocalDateTime timeStamp;

    private Long productId;

    private BigDecimal amount;

    private String currency;

}
