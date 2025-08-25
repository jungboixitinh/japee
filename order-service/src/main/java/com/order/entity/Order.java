package com.order.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`order`")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String userId;
    String fullName;
    String phoneNumber;
    String address;

    Double totalPrice;

    @Enumerated(EnumType.STRING)
    OrderStatus status;
    public enum OrderStatus {
        PENDING, CONFIRMED, CANCELLED, DELIVERED;
    }

    Instant createdAt;
    Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

}
