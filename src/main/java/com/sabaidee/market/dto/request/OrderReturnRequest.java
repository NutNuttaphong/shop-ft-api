package com.sabaidee.market.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderReturnRequest {

    @NotBlank(message = "กรุณากรอกเหตุผลที่ขอคืนสินค้า/คืนเงิน")
    private String returnReason;

    private String returnDescription;
}
