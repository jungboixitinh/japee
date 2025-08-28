package com.review.repository.httpclient;

import com.review.configuration.AuthenticationRequestInterceptor;
import com.review.dto.ApiResponse;
import com.review.dto.response.ReviewProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${app.services.profile}",
    configuration = { AuthenticationRequestInterceptor .class })
public interface ProfileClient {
    @GetMapping(value = "/internal/users/{userId}")
    ApiResponse<ReviewProfileResponse> getProfile(@PathVariable String userId);
}
