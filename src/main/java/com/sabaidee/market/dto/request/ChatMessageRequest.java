package com.sabaidee.market.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatMessageRequest {

    @NotBlank(message = "กรุณาระบุผู้รับ")
    private String receiver;

    @NotBlank(message = "กรุณากรอกข้อความ")
    private String message;
}
