package com.lakepop.reviewService.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final KafkaTemplate<String, String> producer;

    public void sendRequestProductReview(String productId, String review) {
        producer.send("product_review", productId, review);
    }

}
