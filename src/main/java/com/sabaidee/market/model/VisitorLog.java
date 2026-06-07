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
@Document(collection = "visitor_logs")
public class VisitorLog {

    @Id
    private String id;

    private String ipAddress;

    private String sessionId;

    private String pageUrl;

    private String username;

    @CreatedDate
    private Instant createdAt;
}
