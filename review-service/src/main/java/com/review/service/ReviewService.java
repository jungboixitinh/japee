package com.review.service;

import com.review.dto.request.ReviewRequest;
import com.review.dto.response.ReviewResponse;
import com.review.entity.Review;
import com.review.exception.AppException;
import com.review.exception.ErrorCode;
import com.review.repository.ReviewRepository;
import com.review.repository.httpclient.ProductClient;
import com.review.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ReviewRepository reviewRepository;
    ProfileClient profileClient;
    ProductClient productClient;

    public ReviewResponse createReview (ReviewRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var profile = profileClient.getProfile(userId).getResult();
        var product = productClient.getProductById(request.getProductId()).getResult();
        if (profile == null) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        try {
            Review review = Review.builder()
                    .userId(userId)
                    .productId(request.getProductId())
                    .content(request.getContent())
                    .rating(request.getRating())
                    .createdDate(Instant.now())
                    .modifiedDate(Instant.now())
                    .build();

            reviewRepository.save(review);

        ReviewResponse reviewResponse = ReviewResponse.builder()
                .id(review.getId())
                .userId(userId)
                .productId(request.getProductId())
                .username(profile.getUsername())
                .productName(product.getProductName())
                .content(request.getContent())
                .rating(request.getRating())
                .createdDate(review.getCreatedDate())
                .modifiedDate(review.getModifiedDate())
                .build();

            return reviewResponse;
        } catch (AppException e) {
            throw new AppException(ErrorCode.CANNOT_CREATE_REVIEW);
        }
    }

    public List<ReviewResponse> viewReview (String productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) {
            throw new AppException(ErrorCode.REVIEW_NOT_FOUND);
        }

        List<ReviewResponse> reviewResponseList = new ArrayList<>();

        for (Review review : reviews) {

            var profile = profileClient.getProfile(review.getUserId()).getResult();
            var product = productClient.getProductById(review.getProductId()).getResult();
            if (profile == null) {
                throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            }
            if (product == null) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            ReviewResponse reviewResponse = ReviewResponse.builder()
                    .id(review.getId())
                    .userId(review.getUserId())
                    .productId(review.getProductId())
                    .username(profile.getUsername())
                    .productName(product.getProductName())
                    .content(review.getContent())
                    .rating(review.getRating())
                    .createdDate(review.getCreatedDate())
                    .modifiedDate(review.getModifiedDate())
                    .build();
            reviewResponseList.add(reviewResponse);
        }

        return reviewResponseList;
    }

}
