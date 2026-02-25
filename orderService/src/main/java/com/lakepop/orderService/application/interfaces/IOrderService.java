package com.lakepop.orderService.application.interfaces;

import com.lakepop.orderService.application.models.Order;

public interface IOrderService {

    String createOrder(String productId, String username);

    String saveOrder(Order order);

    Order findOrderById(Long id);

}
