package com.sabaidee.market.dto.request;

import com.sabaidee.market.model.CartItem;
import com.sabaidee.market.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {

    @NotBlank(message = "กรุณากรอกชื่อผู้รับ")
    private String customerName;

    @NotBlank(message = "กรุณากรอกเบอร์โทรศัพท์")
    private String customerPhone;

    @NotBlank(message = "กรุณากรอกที่อยู่จัดส่ง")
    private String customerAddress;

    @NotNull(message = "กรุณาเลือกวิธีชำระเงิน")
    private PaymentMethod paymentMethod;

    private boolean slipUploaded;

    private String slipName;

    private List<CartItem> items;

    private String promoCode;

    private double discount;

    private double coinsUsed;

    private String taxName;

    private String taxId;

    private String taxAddress;

    private Boolean taxInvoiceRequested;
}
