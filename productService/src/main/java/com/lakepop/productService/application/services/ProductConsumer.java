package com.lakepop.productService.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductConsumer {

    private final ProductService productService;

    @KafkaListener(topicPartitions = @TopicPartition(partitions = {"0"}, topic = "product_review"), groupId = "reviewsTopics")
    private void handleProductReviewRequest(ConsumerRecord<String, String> record) {
        String productId = record.key();
        String review = record.value();

        try {
            productService.handleReview(Long.parseLong(productId), review);
        } catch (Exception e) {
            log.error("Error while editing reviews from kafka: {}", e.getMessage());
        }
    }


}
