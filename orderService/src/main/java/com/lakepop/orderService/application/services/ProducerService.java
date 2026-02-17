package com.lakepop.orderService.application.services;

import com.lakepop.orderService.application.models.events.InvoiceCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, InvoiceCreatedEvent> invoiceProducer;
    private final KafkaTemplate<String, String> simpleProducer;

    public void sendCreatedOrder(String orderId, String username) {
        simpleProducer.send("created_order", username, orderId);
    }

    public void sendInvoiceCreated(String orderId, InvoiceCreatedEvent event) {
        invoiceProducer.send("created_invoice", orderId, event);
    }

    public void getProductPrice(String orderId, String productId) {
        simpleProducer.send("get_product_price", orderId, productId);
    }

}
