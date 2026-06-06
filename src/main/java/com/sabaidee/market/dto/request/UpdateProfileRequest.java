package com.sabaidee.market.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String displayName;
    private String phone;
    private String address;
}
