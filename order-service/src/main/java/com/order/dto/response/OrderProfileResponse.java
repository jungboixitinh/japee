package com.order.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProfileResponse {
    String id;
    String username;
    String email;
    String fullName;
    String phoneNumber;
    LocalDate dob;
    String address;
}
