package com.sabaidee.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerInfo {
    private String name;
    private String phone;
    private String address;
    private String taxName;
    private String taxId;
    private String taxAddress;
    private Boolean taxInvoiceRequested;
}
