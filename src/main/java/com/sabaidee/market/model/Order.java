package com.sabaidee.market.model;

import com.sabaidee.market.model.enums.OrderStatus;
import com.sabaidee.market.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String orderNo;

    private String userId;

    private List<OrderItem> items;

    private double total;

    private CustomerInfo customer;

    private PaymentMethod paymentMethod;

    private boolean slipUploaded;

    private String slipName;

    private double discount;

    private double coinsUsed;

    private String promoCode;

    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    private String trackingNumber;

    private String carrier;

    private String returnReason;

    private String returnDescription;

    private boolean disputeOpened;

    private String disputeReason;

    private String disputeStatus; // NONE, PENDING, RESOLVED

    @CreatedDate
    private Instant createdAt;
}
