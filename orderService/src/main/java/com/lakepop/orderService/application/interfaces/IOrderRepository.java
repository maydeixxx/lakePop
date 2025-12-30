package com.lakepop.orderService.application.interfaces;

import com.lakepop.orderService.infrastructure.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {
}
