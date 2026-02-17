package com.lakepop.orderService.application.configs;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lakepop.orderService.application.models.events.InvoiceCreatedEvent;
import com.lakepop.orderService.application.models.events.PriceResponseEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class ListenerConfig {

    @Bean
    public ConsumerFactory<String, PriceResponseEvent> priceResponseEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        JacksonJsonDeserializer<PriceResponseEvent> deserializer = new JacksonJsonDeserializer<>(PriceResponseEvent.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PriceResponseEvent> kafkaListenerPriceResponseEvent(ConsumerFactory<String, PriceResponseEvent> priceResponseEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PriceResponseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(priceResponseEventConsumerFactory);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, InvoiceCreatedEvent> invoiceCreatedEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JacksonJsonDeserializer<InvoiceCreatedEvent> deserializer = new JacksonJsonDeserializer<>(InvoiceCreatedEvent.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InvoiceCreatedEvent> kafkaListenerInvoiceCreatedEvent(ConsumerFactory<String, InvoiceCreatedEvent> invoiceCreatedEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, InvoiceCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(invoiceCreatedEventConsumerFactory);

        return factory;
    }

}
