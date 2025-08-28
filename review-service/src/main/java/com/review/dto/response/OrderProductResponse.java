package com.review.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductResponse {
    String id;
    String productName;
    Double price;
    Integer quantity;
    String userId;
    Instant createdDate;
    Instant modifiedDate;
}
