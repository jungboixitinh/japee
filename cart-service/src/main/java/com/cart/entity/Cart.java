package com.cart.entity;

import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Document(value = "cart")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @MongoId
    String id;

    @OneToOne
    String userId;

    @Builder.Default
    List<CartItem> items = new ArrayList<>();

    Instant createdAt;
    Instant updatedAt;
}
