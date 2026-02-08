package com.lakePop.userService.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;


@RequiredArgsConstructor
@Slf4j
public class UserConsumer {

    private final UserService userService;

    @KafkaListener(topicPartitions = @TopicPartition(topic = "created_order", partitions = {"0"}), groupId = "userService")
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

        userService.addOrder(
                username,
                Long.valueOf(orderId)
        );
    }

}
