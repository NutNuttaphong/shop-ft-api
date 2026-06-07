package com.sabaidee.market.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderDisputeRequest {

    @NotBlank(message = "กรุณากรอกเหตุผลในการเปิดข้อพิพาท")
    private String disputeReason;
}
