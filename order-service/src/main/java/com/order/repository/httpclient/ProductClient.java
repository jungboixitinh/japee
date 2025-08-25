package com.order.repository.httpclient;

import com.order.configuration.AuthenticationRequestInterceptor;
import com.order.dto.ApiResponse;
import com.order.dto.response.OrderProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = { AuthenticationRequestInterceptor.class })
public interface ProductClient {
    @GetMapping("/products/{productId}")
    ApiResponse<OrderProductResponse> getProductById(@PathVariable String productId);

    @PutMapping("/products/destock/{productId}")
    ApiResponse<OrderProductResponse> deStockProduct(@PathVariable String productId,@RequestParam Integer quantity);

    @PutMapping("/products/restock/{productId}")
    ApiResponse<OrderProductResponse> reStockProduct(@PathVariable String productId,@RequestParam Integer quantity);

    @GetMapping("/products/stock/{productId}")
    ApiResponse<Boolean> stockProduct(@PathVariable String productId,@RequestParam Integer quantity);
}