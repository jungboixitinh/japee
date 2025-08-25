package com.order.dto.request;

import com.order.entity.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {
    private Order.OrderStatus status;
}
