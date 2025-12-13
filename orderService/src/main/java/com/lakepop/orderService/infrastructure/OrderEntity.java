package com.lakepop.orderService.infrastructure;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "date")
    private LocalDateTime timeStamp;

    @Column(name = "productId")
    private Long productId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

}
