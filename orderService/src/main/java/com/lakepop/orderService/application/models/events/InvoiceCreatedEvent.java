package com.lakepop.orderService.application.models.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceCreatedEvent {
    private Long productId;
    private BigDecimal amount;
    private String payUrl;
}
