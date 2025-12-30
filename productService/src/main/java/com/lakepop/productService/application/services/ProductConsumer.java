package com.lakepop.productService.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductConsumer {
    private final ProductService productService;
    private final ProductProducer producer;

    /**
     * Метод обрабатывающий запрос с добавлением отзыва из кафки
     * Topic = product_review
     * Partition = 0
     * @param record запись полученная из кафки
     */
    @KafkaListener(topicPartitions = @TopicPartition(partitions = {"0"}, topic = "product_review"), groupId = "reviewsTopics")
    private void handleProductReviewRequest(ConsumerRecord<String, String> record) {
        String productId = record.key();
        String review = record.value();

        if (productId == null) {
            log.error("Product id is null");
            throw new NullPointerException();
        }

        if (review == null) {
            log.error("Review is null");
            throw new NullPointerException();
        }

        try {
            productService.handleReview(Long.parseLong(productId), review);
        } catch (Exception e) {
            log.error("Error while editing reviews from kafka: {}", e.getMessage());
        }
    }

    /**
     * Метод для обработки запроса на получение цены товара
     * @param record request id для future и product id для поиска цены
     */
    @KafkaListener(topicPartitions = @TopicPartition(topic = "get_product_price", partitions = {"0"}), groupId = "productService")
    private void handleProductPriceRequest(ConsumerRecord<String, String> record) {
        String requestId = record.key();
        String productId = record.value();

        if (requestId == null) {
            throw new NullPointerException("Request id is null");
        }

        if (productId == null) {
            throw new NullPointerException("Request id or product id is null");
        }

        log.info("Получили сообщение {}", record);

        BigDecimal productPrice = productService.getProductById(Long.parseLong(productId)).getProductPrice();

        producer.sendResponseProductPrice(requestId, String.valueOf(productPrice));
    }



}
