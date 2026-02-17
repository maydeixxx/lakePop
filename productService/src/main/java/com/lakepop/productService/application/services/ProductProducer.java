package com.lakepop.productService.application.services;

import com.lakepop.productService.application.models.PriceResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductProducer {

    private final KafkaTemplate<String, PriceResponseEvent> producer;

    public void sendResponseProductPrice(String productId, PriceResponseEvent event) {
        try {
            producer.send("response_product_price", productId, event);
        } catch (RuntimeException e) {
            log.error("Error in send request product price");
            throw new RuntimeException(e);
        }
    }

}
