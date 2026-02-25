package com.lakePop.userService.application.config;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lakePop.userService.api.models.OrderDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaListenerConfig {

    @Bean
    public ConsumerFactory<String, OrderDTO> orderDTOConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JacksonJsonDeserializer<OrderDTO> deserializer = new JacksonJsonDeserializer<>(OrderDTO.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDTO> orderDTOConcurrentKafkaListenerContainerFactory(ConsumerFactory<String, OrderDTO> orderDTOConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderDTOConsumerFactory);

        return factory;
    }

}
