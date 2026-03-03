package com.lakepop.orderService.api.dto;


import com.lakepop.orderService.application.models.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {

    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String payUrl;

    private LocalDateTime timeStamp;

    private Long productId;

    private BigDecimal amount;

    private String currency;

    private String ownerName;

}
