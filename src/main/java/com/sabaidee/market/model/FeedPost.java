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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feed_posts")
public class FeedPost {

    @Id
    private String id;

    private String caption;

    private String imageUrl;

    private String productId;

    private int likeCount;

    @CreatedDate
    private Instant createdAt;
}
