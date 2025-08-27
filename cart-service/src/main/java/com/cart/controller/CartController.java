package com.cart.controller;

import com.cart.dto.ApiResponse;
import com.cart.dto.request.CartRequest;
import com.cart.dto.request.ReduceItemsRequest;
import com.cart.dto.response.CartResponse;
import com.cart.dto.response.OrderResponse;
import com.cart.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping("/add")
    ApiResponse<CartResponse> addToCart(@RequestBody CartRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addToCart(request))
                .build();
    }

    @GetMapping("/open/{userId}")
    ApiResponse<CartResponse> openCart(@PathVariable String userId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart(userId))
                .build();
    }

    @PostMapping("/reduce")
    ApiResponse<CartResponse> reduceItem(@RequestBody ReduceItemsRequest request) {
        return  ApiResponse.<CartResponse>builder()
                .result(cartService.reduceItem(request))
                .build();
    }

    @PostMapping("order")
    ApiResponse<OrderResponse> placeOrder() {
        return ApiResponse.<OrderResponse>builder()
                .result(cartService.placeOrder())
                .build();
    }
}