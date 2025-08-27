package com.cart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
