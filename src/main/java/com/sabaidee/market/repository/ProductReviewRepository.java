package com.sabaidee.market.repository;

import com.sabaidee.market.model.ProductReview;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductReviewRepository extends MongoRepository<ProductReview, String> {
    List<ProductReview> findByProductIdOrderByCreatedAtDesc(String productId);
    List<ProductReview> findByProductId(String productId);
}
