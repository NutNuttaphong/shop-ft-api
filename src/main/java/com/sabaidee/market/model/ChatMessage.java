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
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;

    private String sender; // username

    private String receiver; // username

    private String message;

    private String mediaUrl;

    private String mediaType;

    @CreatedDate
    private Instant timestamp;
}
