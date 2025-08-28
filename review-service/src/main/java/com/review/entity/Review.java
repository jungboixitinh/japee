package com.review.entity;

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
@Document(value = "review")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    @MongoId
    String id;
    String userId;
    String productId;
    String content;
    Integer rating;
    Instant createdDate;
    Instant modifiedDate;
}
