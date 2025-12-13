package com.lakepop.orderService.application.interfaces;

import com.lakepop.orderService.api.dto.OrderDTO;
import com.lakepop.orderService.application.models.Order;
import com.lakepop.orderService.infrastructure.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IOrderMapper {

    Order orderEntityToDomain(OrderEntity orderEntity);

    Order orderDtoToDomain(OrderDTO orderDTO);

}
