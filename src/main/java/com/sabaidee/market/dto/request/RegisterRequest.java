package com.sabaidee.market.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "กรุณากรอกชื่อผู้ใช้")
    @Size(min = 3, max = 50, message = "ชื่อผู้ใช้ต้องมีความยาว 3-50 ตัวอักษร")
    private String username;

    @NotBlank(message = "กรุณากรอกรหัสผ่าน")
    @Size(min = 6, message = "รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร")
    private String password;

    @NotBlank(message = "กรุณากรอกชื่อแสดงผล")
    private String displayName;

    private String phone;

    private String address;
}
