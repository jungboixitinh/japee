package com.order.mapper;

import com.order.dto.response.OrderItemResponse;
import com.order.dto.response.OrderResponse;
import com.order.entity.Order;
import com.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    // map Order → OrderResponse
    @Mapping(source = "status", target = "status") // status là Enum → tự động convert sang String
    OrderResponse toOrderResponse(Order order);

    // map OrderItem → OrderItemResponse
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
