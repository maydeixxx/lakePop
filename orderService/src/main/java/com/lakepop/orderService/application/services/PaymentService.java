package com.lakepop.orderService.application.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakepop.orderService.application.models.events.InvoiceCreatedEvent;
import com.lakepop.orderService.application.models.events.PriceResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${cryptoTestNet.url}")
    private String url;

    @Value("${cryptoTestNet.apiKey}")
    private String apiKey;

    private final ProducerService producerService;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    @KafkaListener(
            topicPartitions = @TopicPartition(topic = "response_product_price", partitions = {"0"}),
            groupId = "paymentService",
            containerFactory = "kafkaListenerPriceResponseEvent"
    )
    public void createInvoice(ConsumerRecord<String, PriceResponseEvent> record) {
        String pay_url = null;
        String productId = record.key();
        PriceResponseEvent event = record.value();
        String orderId = event.getOrderId();
        BigDecimal amount = event.getAmount();

        try {
            Map<String, String> mapBody = Map.of(
                    "asset", "USDT",
                    "amount", amount.toString(),
                    "description", "payment for product [" + productId + "]"
            );

            HttpRequest.BodyPublisher stringBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(mapBody));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url + "/createInvoice"))
                    .header("Crypto-Pay-API-Token", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(stringBody)
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode jsonNode = objectMapper.readTree(response.body());
            log.info(String.valueOf(jsonNode));

            if (jsonNode.has("result")) {
                JsonNode result = jsonNode.get("result");
                pay_url = result.get("pay_url").asText();
            } else {
                log.error("Didnt receive pay url");
            }

            log.info(pay_url);

        } catch (Exception e) {
            log.error("Error in createInvoice: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        producerService.sendInvoiceCreated(orderId, InvoiceCreatedEvent.builder()
                                                    .productId(Long.parseLong(productId))
                                                    .payUrl(pay_url)
                                                    .amount(amount)
                                                    .build()
        );
    }

}
