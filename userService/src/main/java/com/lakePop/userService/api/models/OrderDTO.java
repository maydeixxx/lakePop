package com.lakePop.userService.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {

    private Long id;

    private String orderStatus; //CREATED, PAID, CANCELED, AWAITING_PAYMENT

    private String payUrl;

    private LocalDateTime timeStamp;

    private Long productId;

    private BigDecimal amount;

    private String currency;

}
