package com.lakepop.productService.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductProducer {

    private final KafkaTemplate<String, String> producer;

    public void sendResponseProductPrice(String requestId, String price) {
        try {
            producer.send("response_product_price", requestId, price);
        } catch (RuntimeException e) {
            log.error("Error in send request product price");
            throw new RuntimeException(e);
        }
    }

}
