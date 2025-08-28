package com.review.repository.httpclient;

import com.review.configuration.AuthenticationRequestInterceptor;
import com.review.dto.ApiResponse;
import com.review.dto.response.OrderProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = { AuthenticationRequestInterceptor.class })
public interface ProductClient {
    @GetMapping("/products/{productId}")
    ApiResponse<OrderProductResponse> getProductById(@PathVariable String productId);
}