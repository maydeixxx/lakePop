//package com.lakepop.reviewService.services;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//@Configuration
//public class Config {
//
//    @Bean
//    public KafkaTemplate<String, String> initKafkaTemplate(ProducerFactory<String, String> producerFactory) {
//        return new KafkaTemplate<>(producerFactory);
//    }
//
//    @Bean
//    public ReviewService initReviewService(KafkaTemplate<String, String> producer) {
//        return new ReviewService(producer);
//    }
//
//}
