package com.lakepop.orderService.infrastructure;

import com.lakepop.orderService.application.models.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
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
