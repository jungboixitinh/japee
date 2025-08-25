package com.product.service;

import com.product.dto.request.ProductCreationRequest;
import com.product.dto.response.ProductResponse;
import com.product.entity.Product;
import com.product.exception.AppException;
import com.product.exception.ErrorCode;
import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;

    @Transactional(rollbackFor = AppException.class)
    public ProductResponse createProduct(ProductCreationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Product product = Product.builder()
                .productName(request.getProductName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .userId(authentication.getName())
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        product = productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public List<ProductResponse> getMyProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return productRepository.findAllByUserId(userId)
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    public ProductResponse getProductById(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return productMapper.toProductResponse(productRepository.findById(id).get());
    }

    public boolean checkProductStock(String id, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getQuantity() >= quantity) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional(rollbackFor = AppException.class)
    public ProductResponse deProductStock(String id, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        product.setQuantity(product.getQuantity() - quantity);
        if (product.getQuantity() < 0) {
            throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);
        }
        product.setModifiedDate(Instant.now());
        product = productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    @Transactional(rollbackFor = AppException.class)
    public ProductResponse reStockProduct(String id, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        product.setQuantity(product.getQuantity() + quantity);
        product.setModifiedDate(Instant.now());
        product = productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

}
