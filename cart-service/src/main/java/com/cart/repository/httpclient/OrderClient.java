package com.cart.repository.httpclient;

import com.cart.configuration.AuthenticationRequestInterceptor;
import com.cart.dto.ApiResponse;
import com.cart.dto.request.OrderCreationRequest;
import com.cart.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service", url = "${app.services.order}",
        configuration = { AuthenticationRequestInterceptor.class })
public interface OrderClient {
    @PostMapping("/orders/place")
    ApiResponse<OrderResponse> createOrder(@RequestBody OrderCreationRequest request);
}