package com.product.controller;

import com.product.dto.ApiResponse;
import com.product.dto.request.ProductCreationRequest;
import com.product.dto.response.ProductResponse;
import com.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping("/create")
    ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreationRequest request){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request))
                .build();
    }

    @GetMapping("/products")
    ApiResponse<List<ProductResponse>> myProducts(){
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getMyProducts())
                .build();
    }

    @GetMapping("/products/{productId}")
    ApiResponse<ProductResponse> getProductById(@PathVariable String productId) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProductById(productId))
                .build();
    }

    @GetMapping("/products/stock/{productId}")
    ApiResponse<Boolean> stockProduct(@PathVariable String productId,@RequestParam Integer quantity) {
        return ApiResponse.<Boolean>builder()
                .result(productService.checkProductStock(productId, quantity))
                .build();
    };

    @PutMapping("/products/destock/{productId}")
    ApiResponse<ProductResponse> deStockProduct(@PathVariable String productId,@RequestParam Integer quantity) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.deProductStock(productId, quantity))
                .build();
    };

    @PutMapping("/products/restock/{productId}")
    ApiResponse<ProductResponse> reStockProduct(@PathVariable String productId,@RequestParam Integer quantity) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.reStockProduct(productId, quantity))
                .build();
    };
}