package com.product.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Getter
@Setter
@Builder
@Document(value = "product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @MongoId
    String id;
    String userId;
    String productName;
    Double price;
    Integer quantity;
    Instant createdDate;
    Instant modifiedDate;
}
