package com.lakepop.orderService.application.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakepop.orderService.application.interfaces.IOrderMapper;
import com.lakepop.orderService.application.interfaces.IOrderRepository;
import com.lakepop.orderService.application.interfaces.IOrderService;
import com.lakepop.orderService.application.models.Order;
import com.lakepop.orderService.infrastructure.OrderEntity;
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
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {

    @Value("${cryptoTestNet.url}")
    private String url;

    @Value("${cryptoTestNet.apiKey}")
    private String apiKey;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final IOrderMapper orderMapper;

    private final IOrderRepository orderRepository;

    private final ProducerService producerService;

    private final Map<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    /**
     * Метод для создания счета на оплату (пока что тест) || валюта (USDT, TRX тд и тп)
     *
     * @param amount цена товара
     * @return возвращает String ссылку на оплату счёта
     */
    @Override
    public String createInvoice(String amount, String productId) {
        String pay_url = null;

        try {
            Map<String, String> mapBody = Map.of(
                    "asset", "USDT",
                    "amount", amount,
                    "description", "payment for product [" + productId + "]"
            );

            BodyPublisher stringBody = BodyPublishers.ofString(objectMapper.writeValueAsString(mapBody));

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

        return pay_url;
    }

    /**
     * метод создания заказа
     *
     * @param userId    id пользователя, который заказывает товар
     * @param productId id товара
     * @return ссылка на оплату товара
     */
    @Override
    public String createOrder(String userId, String productId) {
        String requestId = UUID.randomUUID().toString();

        CompletableFuture<String> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        producerService.getProductPrice(requestId, productId);

        String amount;

        try {
            amount = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("Timeout from product-service: {}", requestId);
            pendingRequests.remove(requestId);
            throw new RuntimeException(e.getCause());

        } catch (ExecutionException e) {
            log.error("Error while receiving price: {}", e.getCause().getMessage());
            pendingRequests.remove(requestId);
            throw new RuntimeException(e.getCause());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            pendingRequests.remove(requestId);
            throw new RuntimeException("Process interrupted");
        }

        String orderId = saveOrder(
                Order.builder()
                        .amount(BigDecimal.valueOf(Double.parseDouble(amount)))
                        .currency("USDT")
                        .timeStamp(LocalDateTime.now())
                        .productId(Long.parseLong(productId))
                        .build()
        );

        producerService.sendCreatedOrder(orderId, userId);

        return createInvoice(amount, productId);
    }

    /**
     * сохранение заказа
     * @param order доменная модель заказа
     * @return id сохраненного заказа
     */
    @Override
    public String saveOrder(Order order) {
        OrderEntity saved = orderRepository.save(orderMapper.orderDomainToEntity(order));
        return saved.getId().toString();
    }

    /**
     * получение ответа на запрос цены товара
     *
     * @param record данные из kafka (requestId для future и цена товара)
     */
    @KafkaListener(topicPartitions = @TopicPartition(topic = "response_product_price", partitions = {"0"}), groupId = "orderService")
    private void handleProductPriceResponse(ConsumerRecord<String, String> record) {
        String requestId = record.key();
        String amount = record.value();

        if (requestId == null) {
            throw new NullPointerException("request id is null");
        }

        if (amount == null) {
            throw new NullPointerException("amount is null");
        }

        CompletableFuture<String> future = pendingRequests.remove(requestId);

        if (future != null) {
            future.complete(amount);
        } else {
            log.error("Didnt receive product price");
        }
    }

}