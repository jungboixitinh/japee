package com.notification.dto.response;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    String username;
    String email;
    String fullName;
    String phoneNumber;
    LocalDate dob;
    String address;
}
