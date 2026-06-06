package com.sabaidee.market.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "กรุณากรอกชื่อสินค้า")
    private String name;

    @Positive(message = "ราคาต้องมากกว่า 0")
    private double price;

    private String description;

    @NotBlank(message = "กรุณาเลือกหมวดหมู่")
    private String category;

    private String imageUrl;

    @Min(value = 0, message = "จำนวนสต็อกต้องไม่ติดลบ")
    private int stock;
}
