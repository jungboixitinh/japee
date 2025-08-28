package com.review.repository;

import com.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    Page<Review> findAllByUserId(String userId, Pageable pageable);

    List<Review> findByProductId(String productId);
}
