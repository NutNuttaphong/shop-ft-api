package com.sabaidee.market.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotBlank(message = "กรุณาระบุรหัสสินค้า")
    private String productId;

    @Min(value = 1, message = "จำนวนต้องมากกว่า 0")
    private int quantity;

    private String variantName;

    private double priceAdjustment;
}
