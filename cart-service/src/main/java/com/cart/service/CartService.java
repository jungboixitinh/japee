package com.cart.service;

import com.cart.dto.request.*;
import com.cart.dto.response.CartResponse;
import com.cart.dto.response.OrderResponse;
import com.cart.entity.Cart;
import com.cart.entity.CartItem;
import com.cart.exception.AppException;
import com.cart.exception.ErrorCode;
import com.cart.mapper.CartMapper;
import com.cart.repository.CartRepository;
import com.cart.repository.httpclient.OrderClient;
import com.cart.repository.httpclient.ProductClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    CartMapper cartMapper;
    ProductClient productClient;
    OrderClient orderClient;

    public CartResponse addToCart (CartRequest request){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> Cart.builder()
                .userId(userId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        for (CartItemsRequest itemReq : request.getItems()) {
            var product = productClient.getProductById(itemReq.getProductId());
            boolean stock = productClient.stockProduct(itemReq.getProductId(), itemReq.getQuantity()).getResult();

            if (!stock) {
                throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);
            }

            CartItem existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(itemReq.getProductId()))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + itemReq.getQuantity());
            } else {
                CartItem newItem = CartItem.builder()
                        .productId(itemReq.getProductId())
                        .productName(product.getResult().getProductName())
                        .price(product.getResult().getPrice())
                        .quantity(itemReq.getQuantity())
                        .build();

                cart.getItems().add(newItem);
            }
        }

        cart.setUpdatedAt(Instant.now());
        cart = cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    public CartResponse getCart (String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return cartMapper.toCartResponse(cart);
    }

    public CartResponse reduceItem (ReduceItemsRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            if (existingItem.getQuantity() < request.getQuantity()) {
                throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);
            } else if (existingItem.getQuantity().equals(request.getQuantity())) {
                cart.getItems().remove(existingItem);
            } else {
                existingItem.setQuantity(existingItem.getQuantity() - request.getQuantity());
            }
        } else {
            throw new AppException(ErrorCode.ITEM_NOT_FOUND);
        }
        cart.setUpdatedAt(Instant.now());
        cart = cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    public OrderResponse placeOrder() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.ITEM_NOT_FOUND);
        }

        List<OrderItemRequest> orderItems = cart.getItems().stream()
                .map(item -> OrderItemRequest.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        OrderCreationRequest request = OrderCreationRequest.builder()
                .items(orderItems)
                .build();

        OrderResponse response = orderClient.createOrder(request).getResult();

        if (response == null) {
            throw new AppException(ErrorCode.ORDER_CREATION_FAILED);
        }

        cart.getItems().clear();
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return response;
    }
}
