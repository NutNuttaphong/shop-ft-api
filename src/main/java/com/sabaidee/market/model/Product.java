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
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;

    private double price;

    private String description;

    private String category;

    private String imageUrl;

    private String videoUrl;

    private int stock;

    @CreatedDate
    private Instant createdAt;
}
