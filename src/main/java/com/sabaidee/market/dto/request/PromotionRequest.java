package com.sabaidee.market.dto.request;

import com.sabaidee.market.model.enums.DiscountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PromotionRequest {

    @NotBlank(message = "กรุณากรอกรหัสโปรโมชัน")
    private String code;

    @NotBlank(message = "กรุณากรอกชื่อโปรโมชัน")
    private String name;

    private String description;

    @NotNull(message = "กรุณาเลือกประเภทส่วนลด")
    private DiscountType discountType;

    @Positive(message = "จำนวนส่วนลดต้องมากกว่า 0")
    private double discountValue;

    @Min(value = 0, message = "ยอดขั้นต่ำต้องไม่ติดลบ")
    private double minPurchase;

    @com.fasterxml.jackson.annotation.JsonProperty("isActive")
    private boolean isActive;

    private String imageUrl;

    private String startDate;

    private String endDate;

    private String type;

    private java.util.List<String> productIds;

    private Integer bundleQty;

    private String targetCategory;
}
