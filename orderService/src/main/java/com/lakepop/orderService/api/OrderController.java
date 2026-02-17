package com.lakepop.orderService.api;

import com.lakepop.orderService.application.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/orderService")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/createInvoice/{productId}")
    private ResponseEntity<?> createOrder(@PathVariable String productId) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        try {
            String invoice = orderService.createOrder(productId, username);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
