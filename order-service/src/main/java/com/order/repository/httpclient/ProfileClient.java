package com.order.repository.httpclient;

import com.order.configuration.AuthenticationRequestInterceptor;
import com.order.dto.ApiResponse;
import com.order.dto.response.OrderProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${app.services.profile}",
    configuration = { AuthenticationRequestInterceptor .class })
public interface ProfileClient {
    @GetMapping(value = "/internal/users/{userId}")
    ApiResponse<OrderProfileResponse> getProfile(@PathVariable String userId);
}
