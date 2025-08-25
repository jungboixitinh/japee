package com.product.repository;

import com.product.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findAllByUserId(String userId);

}
