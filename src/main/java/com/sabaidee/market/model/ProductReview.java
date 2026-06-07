package com.sabaidee.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "product_reviews")
public class ProductReview {

    @Id
    private String id;

    private String productId;

    private String userId;

    private String username;

    private int rating;

    private String comment;

    @CreatedDate
    private Instant createdAt;
}
