package com.sabaidee.market.model;

import com.sabaidee.market.model.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "promotions")
public class Promotion {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private String name;

    private String description;

    private DiscountType discountType;

    private double discountValue;

    private double minPurchase;

    private boolean isActive;

    private String imageUrl;

    private String startDate;

    private String endDate;
}
