package com.review.controller;

import com.review.dto.ApiResponse;
import com.review.dto.PageResponse;
import com.review.dto.request.ReviewRequest;
import com.review.dto.response.ReviewResponse;
import com.review.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;

    @PostMapping("/create")
    ApiResponse<ReviewResponse> createReview(@RequestBody ReviewRequest request){
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.createReview(request))
                .build();
    }

    @GetMapping("/view/{productId}")
    ApiResponse<List<ReviewResponse>> viewReview(@PathVariable String productId) {
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.viewReview(productId))
                .build();
    }

}