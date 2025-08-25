package com.order.controller;

import com.order.dto.request.OrderCreationRequest;
import com.order.dto.request.UpdateOrderStatusRequest;
import com.order.dto.response.OrderResponse;

import com.order.entity.Order;
import org.springframework.web.bind.annotation.*;

import com.order.dto.ApiResponse;
import com.order.service.OrderService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {
    OrderService orderService;

    @PostMapping("/place")
    ApiResponse<OrderResponse> createOrder(@RequestBody OrderCreationRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
    }

    @GetMapping("/my-order/{orderId}")
    ApiResponse<OrderResponse> getOrder(@PathVariable("orderId") String orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrder(orderId))
                .build();
    }

    @PutMapping("/{orderId}")
    ApiResponse<OrderResponse> updateOrderStatus(@PathVariable String orderId,
                                                 @RequestBody UpdateOrderStatusRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId, request))
                .build();
    }
}
