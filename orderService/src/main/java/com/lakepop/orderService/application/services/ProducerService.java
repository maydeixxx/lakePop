package com.lakepop.orderService.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, String> producer;

    public void sendCreatedOrder(String orderId, String username) {
        producer.send("created_order", username, orderId);
    }

    public void getProductPrice(String requestId, String productId) {
        producer.send("get_product_price", requestId, productId);
    }

}
