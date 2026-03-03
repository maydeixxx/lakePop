package com.lakepop.orderService.application.services;

import com.lakepop.orderService.application.models.Order;
import com.lakepop.orderService.application.models.events.InvoiceCreatedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {


    private final KafkaTemplate<String, InvoiceCreatedEvent> invoiceCreatedEventKafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final KafkaTemplate<String, Order> orderKafkaTemplate;


    public ProducerService(
            @Qualifier(value = "invoiceCreatedEventKafkaTemplate")
            KafkaTemplate<String, InvoiceCreatedEvent> invoiceCreatedEventKafkaTemplate,

            @Qualifier(value = "stringKafkaTemplate")
            KafkaTemplate<String, String> stringKafkaTemplate,

            @Qualifier(value = "orderKafkaTemplate")
            KafkaTemplate<String, Order> orderKafkaTemplate
    ) {
        this.invoiceCreatedEventKafkaTemplate = invoiceCreatedEventKafkaTemplate;
        this.stringKafkaTemplate = stringKafkaTemplate;
        this.orderKafkaTemplate = orderKafkaTemplate;
    }

    public void sendCreatedOrder(String orderId, String username) {
        stringKafkaTemplate.send("created_order", orderId, username);
    }

    public void sendInvoiceCreated(String orderId, InvoiceCreatedEvent event) {
        invoiceCreatedEventKafkaTemplate.send("created_invoice", orderId, event);
    }

    public void getProductPrice(String orderId, String productId) {
        stringKafkaTemplate.send("get_product_price", orderId, productId);
    }

    public void sendResponseOrderId(String requestId, Order order) {
        orderKafkaTemplate.send("responseOrderId", requestId, order);
    }

    public void sendDeletedOrderEvent(String ownerName, String id) {
        stringKafkaTemplate.send("deleted_order", ownerName, id);
    }

}
