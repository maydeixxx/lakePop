package com.lakepop.reviewService.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ReviewService {
    private final KafkaTemplate<String, String> producer;

    /**
     * отправляет сообщение в топик product_review для обработки добавления отзыва
     * @param productId id продукта
     * @param review отзыв для продукта
     */
    public void sendRequestProductReview(String productId, String review) {
        producer.send("product_review", 0, productId, review);
    }

}
