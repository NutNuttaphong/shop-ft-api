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
@Document(collection = "shopee_videos")
public class ShopeeVideo {

    @Id
    private String id;

    private String title;

    private String videoUrl;

    private String productId;

    private int viewCount;

    private int likeCount;

    private int clickCount;

    @CreatedDate
    private Instant createdAt;
}
