package com.lakepop.orderService.application.services;

import com.lakepop.orderService.application.models.events.InvoiceCreatedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {


    private final KafkaTemplate<String, InvoiceCreatedEvent> invoiceCreatedEventKafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    public ProducerService(
            @Qualifier(value = "invoiceCreatedEventKafkaTemplate")
            KafkaTemplate<String, InvoiceCreatedEvent> invoiceCreatedEventKafkaTemplate,

            @Qualifier(value = "stringKafkaTemplate")
            KafkaTemplate<String, String> stringKafkaTemplate
    ) {
        this.invoiceCreatedEventKafkaTemplate = invoiceCreatedEventKafkaTemplate;
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    public void sendCreatedOrder(String orderId, String username) {
        stringKafkaTemplate.send("created_order", username, orderId);
    }

    public void sendInvoiceCreated(String orderId, InvoiceCreatedEvent event) {
        invoiceCreatedEventKafkaTemplate.send("created_invoice", orderId, event);
    }

    public void getProductPrice(String orderId, String productId) {
        stringKafkaTemplate.send("get_product_price", orderId, productId);
    }

}
