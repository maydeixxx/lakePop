package com.lakePop.userService.application.services;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserProducer {
    private final KafkaTemplate<String, Long> kafkaTemplate;

    public void requestOrderBody(String requestId, Long orderId) {
        kafkaTemplate.send("requestOrderId", requestId, orderId);
    }

}
