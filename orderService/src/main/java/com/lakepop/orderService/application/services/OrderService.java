package com.lakepop.orderService.application.services;

import com.lakepop.orderService.application.exeptions.OrderNotFoundException;
import com.lakepop.orderService.application.interfaces.IOrderMapper;
import com.lakepop.orderService.application.interfaces.IOrderRepository;
import com.lakepop.orderService.application.interfaces.IOrderService;
import com.lakepop.orderService.application.models.events.InvoiceCreatedEvent;
import com.lakepop.orderService.application.models.Order;
import com.lakepop.orderService.application.models.OrderStatus;
import com.lakepop.orderService.infrastructure.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {

    private final IOrderMapper orderMapper;

    private final IOrderRepository orderRepository;

    private final ProducerService producerService;

    /**
     * метод создания заказа
     *
     * @param productId id товара
     * @return ссылка на оплату товара
     */
    @Override
    public String createOrder(String productId, String username) {

        String orderId = saveOrder(
                Order.builder()
                        .currency("USDT")
                        .timeStamp(LocalDateTime.now())
                        .productId(Long.parseLong(productId))
                        .build()
        );

        producerService.getProductPrice(orderId, productId);
        producerService.sendCreatedOrder(orderId, username);

        return orderId;
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

    @KafkaListener(
            topicPartitions = @TopicPartition(topic = "created_invoice", partitions = {"0"}),
            groupId = "orderService",
            containerFactory = "kafkaListenerInvoiceCreatedEvent"
    )
    @Transactional
    public void handleInvoice(ConsumerRecord<String, InvoiceCreatedEvent> event) {
        String orderId = event.key();
        InvoiceCreatedEvent value = event.value();

        BigDecimal amount = value.getAmount();
        String payUrl = value.getPayUrl();

        OrderEntity orderEntity = orderRepository.findById(Long.parseLong(orderId)).orElseThrow(() -> new OrderNotFoundException(String.format("Order %s not found", orderId)));
        orderEntity.setAmount(amount);
        orderEntity.setOrderStatus(OrderStatus.AWAITING_PAYMENT);
        orderEntity.setPayUrl(payUrl);
    }

}