package com.order.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.event.dto.OrderNotificationEvent;
import com.order.dto.request.OrderCreationRequest;
import com.order.dto.request.OrderItemRequest;
import com.order.dto.request.UpdateOrderStatusRequest;
import com.order.dto.response.OrderProductResponse;
import com.order.entity.Order;
import com.order.entity.OrderItem;
import com.order.exception.AppException;
import com.order.exception.ErrorCode;
import com.order.repository.httpclient.ProductClient;
import com.order.repository.httpclient.ProfileClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.dto.response.OrderResponse;
import com.order.mapper.OrderMapper;
import com.order.repository.OrderRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    ProductClient productClient;
    ProfileClient profileClient;
    KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional(rollbackFor = AppException.class)
    public OrderResponse createOrder(OrderCreationRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var profile = profileClient.getProfile(userId).getResult();

        if (profile == null) {
            throw new AppException(ErrorCode.PROFILE_NOT_EXISTED);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0;
        Map<String, Integer> deductedProducts = new HashMap<>();
        try {
            for (OrderItemRequest item : request.getItems()) {

                OrderProductResponse product = productClient.getProductById(item.getProductId()).getResult();
                if (product == null) {
                    throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
                }

                if (!productClient.stockProduct(product.getId(), item.getQuantity()).getResult()) {
                    throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);
                }

                productClient.deStockProduct(product.getId(), item.getQuantity());
                deductedProducts.put(product.getId(), item.getQuantity());
                totalPrice += product.getPrice() * item.getQuantity();

                OrderItem orderItem = OrderItem.builder()
                        .productId(product.getId())
                        .productName(product.getProductName())
                        .quantity(item.getQuantity())
                        .price(product.getPrice())
                        .build();
                orderItems.add(orderItem);
            }

            Order order = Order.builder()
                    .userId(profile.getId())
                    .fullName(profile.getFullName())
                    .phoneNumber(profile.getPhoneNumber())
                    .address(profile.getAddress())
                    .status(Order.OrderStatus.PENDING)
                    .totalPrice(totalPrice)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            for (OrderItem item : orderItems) {
                order.getItems().add(item);
                item.setOrder(order);
            }

            order = orderRepository.save(order);
            OrderResponse orderResponse = orderMapper.toOrderResponse(order);

            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append("<h2>Hello, ").append(profile.getUsername()).append("</h2>")
                    .append("<p>Your order was placed successfully 🎉</p>")
                    .append("<p><b>Order ID:</b> ").append(orderResponse.getId()).append("</p>")
                    .append("<p><b>Total Price:</b> ").append(orderResponse.getTotalPrice()).append("</p>")
                    .append("<p><b>Status:</b> ").append(orderResponse.getStatus()).append("</p>")
                    .append("<p><b>Items:</b></p>")
                    .append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse:collapse;'>")
                    .append("<tr><th>Product</th><th>Quantity</th><th>Price</th><th>Total</th></tr>");

            for (var item : orderResponse.getItems()) {
                bodyBuilder.append("<tr>")
                        .append("<td>").append(item.getProductName()).append("</td>")
                        .append("<td>").append(item.getQuantity()).append("</td>")
                        .append("<td>").append(item.getPrice()).append("</td>")
                        .append("<td>").append(item.getQuantity() * item.getPrice()).append("</td>")
                        .append("</tr>");
            }
            bodyBuilder.append("</table>");

            OrderNotificationEvent notificationEvent = OrderNotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(profile.getEmail())
                    .subject("Place an order successfully")
                    .body(bodyBuilder.toString())
                    .build();

            kafkaTemplate.send("order-delivery", notificationEvent);

            return orderMapper.toOrderResponse(order);
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());

            deductedProducts.forEach((productId, quantity) -> {
                try {
                    productClient.reStockProduct(productId, quantity);
                } catch (Exception reStockException) {
                    log.error("Failed to de-stock product {}: {}", productId, reStockException.getMessage());
                }
            });
            throw new AppException(ErrorCode.ORDER_CREATION_FAILED);

        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        order.setStatus(request.getStatus());
        order.setUpdatedAt(Instant.now());
        order = orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse getOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        return orderMapper.toOrderResponse(order);
    }
}
