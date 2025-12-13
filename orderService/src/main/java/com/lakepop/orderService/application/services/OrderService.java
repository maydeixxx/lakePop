package com.lakepop.orderService.application.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.lakepop.orderService.application.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {

    @Value("${cryptoTestNet.url}")
    private String url;

    @Value("${cryptoTestNet.apiKey}")
    private String apiKey;

    private final HttpClient httpClient;

    private final ObjectMapper mapper;

    /**
     * Метод для создания счета на оплату (пока что тест)
     * @param asset валюта (USDT, TRX тд и тп)
     * @param amount кол-во
     * @param description описание счета, для чего он(к примеру покупка продукта)
     * @return возвращает String ссылку на оплату счёта
     */
    @Override
    public String createInvoice(String asset, String amount, String description) {
        String pay_url = null;

        try {
            Map<String, String> mapBody = Map.of(
                    "asset", asset,
                    "amount", amount,
                    "description", description
            );

            BodyPublisher stringBody = BodyPublishers.ofString(mapper.writeValueAsString(mapBody));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url + "/createInvoice"))
                    .header("Crypto-Pay-API-Token", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(stringBody)
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode jsonNode = mapper.readTree(response.body());
            log.info(String.valueOf(jsonNode));

            if (jsonNode.has("result")) {
                JsonNode result = jsonNode.get("result");
                pay_url = result.get("pay_url").asText();
            }

            log.info(pay_url);

        } catch (Exception e) {
            log.error("Error in createInvoice: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return pay_url;
    }

}
