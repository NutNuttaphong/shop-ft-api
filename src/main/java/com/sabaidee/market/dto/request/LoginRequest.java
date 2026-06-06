package com.sabaidee.market.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "กรุณากรอกชื่อผู้ใช้")
    private String username;

    @NotBlank(message = "กรุณากรอกรหัสผ่าน")
    private String password;
}
