package com.lakePop.userService.application;

import com.lakePop.userService.api.models.UserUpdateDTO;
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

        if (orderId == null || username == null) {
            log.error("Username or order id is null");
            throw new NullPointerException();
        }

        userService.updateUser(
                username,
                UserUpdateDTO.builder()
                        .field("orders")
                        .orderId(Long.parseLong(orderId))
                        .build()
        );
    }

}
