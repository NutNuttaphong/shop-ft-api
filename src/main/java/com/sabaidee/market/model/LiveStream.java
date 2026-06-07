package com.sabaidee.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "live_streams")
public class LiveStream {

    @Id
    private String id;

    private String title;

    private String status; // STREAMING, ENDED

    private List<String> productIds;

    private String pinnedProductId;

    private int viewerCount;

    private int likeCount;

    @CreatedDate
    private Instant createdAt;
}
