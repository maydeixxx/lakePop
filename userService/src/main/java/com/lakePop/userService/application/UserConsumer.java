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
        String email = record.value();

        if (orderId == null || email == null) {
            log.error("User email or order id is null");
            throw new NullPointerException();
        }

        userService.updateUser(
                UserUpdateDTO.builder()
                        .keyWord("email")
                        .email(email)
                        .field("orders")
                        .orderId(Long.parseLong(orderId))
                        .build()
        );
    }

}
