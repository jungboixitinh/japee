package com.cart.repository.httpclient;

import com.cart.configuration.AuthenticationRequestInterceptor;
import com.cart.dto.ApiResponse;
import com.cart.dto.response.OrderProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = { AuthenticationRequestInterceptor.class })
public interface ProductClient {
    @GetMapping("/products/{productId}")
    ApiResponse<OrderProductResponse> getProductById(@PathVariable String productId);

    @GetMapping("/products/stock/{productId}")
    ApiResponse<Boolean> stockProduct(@PathVariable String productId,@RequestParam Integer quantity);
}