package com.sabaidee.market.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductReviewRequest {

    @Min(value = 1, message = "คะแนนต้องอยู่ระหว่าง 1 ถึง 5")
    @Max(value = 5, message = "คะแนนต้องอยู่ระหว่าง 1 ถึง 5")
    private int rating;

    @NotBlank(message = "กรุณากรอกความคิดเห็น")
    private String comment;
}
