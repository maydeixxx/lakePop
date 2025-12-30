package com.lakepop.orderService.application.interfaces;

import com.lakepop.orderService.application.models.Order;

public interface IOrderService {

    String createInvoice(String amount, String productId);

    String createOrder(String userId, String productId);

    String saveOrder(Order order);

}
