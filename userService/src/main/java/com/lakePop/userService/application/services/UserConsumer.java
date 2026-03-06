package com.lakePop.userService.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserConsumer {

    private final UserService userService;

    /**
     * Обработка созданного order
     * @param record модель из orderService
     */
    @KafkaListener(
            topicPartitions = @TopicPartition(topic = "created_order", partitions = {"0"}),
            groupId = "userService",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    private void handleCreatedOrder(ConsumerRecord<String, String> record) {
        String orderId = record.key();
        String username = record.value();

        if (username == null) {
            log.error("Username is null");
            throw new NullPointerException();
        }

        if (orderId == null) {
            log.error("Order id is null");
            throw new NullPointerException();
        }
        try {
            userService.addOrder(
                    username,
                    Long.valueOf(orderId)
            );
        } catch (Exception e) {
            throw new KafkaException(e.getMessage());
        }
    }

    /**
     * Обработка удаления order
     * @param record record model
     */
    @KafkaListener(
            topicPartitions = @TopicPartition(topic = "deleted_order", partitions = {"0"}),
            groupId = "userService",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    private void handleDeletedOrderEvent(ConsumerRecord<String, String> record) {
        String orderId = record.value();
        String userName = record.key();

        if (orderId == null) {
            throw new KafkaException("Order id is null");
        }

        if (userName == null) {
            throw new KafkaException("username is null");
        }

        userService.removeOrder(userName, Long.parseLong(orderId));
    }


}
