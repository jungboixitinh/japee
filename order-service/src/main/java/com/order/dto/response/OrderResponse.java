package com.order.dto.response;

import java.util.List;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    String userId;
    String fullName;
    String phoneNumber;
    String address;
    Double totalPrice;
    String status;
    List<OrderItemResponse> items;
}
