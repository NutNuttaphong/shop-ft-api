package com.sabaidee.market.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private double totalSales;
    private long totalVisitors;
    private double conversionRate;
    private long totalOrders;
    
    private List<DailySales> dailySales;
    private List<CategorySales> categorySales;
    private List<PaymentMethodSales> paymentMethodSales;
    private List<TopProduct> topProducts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySales {
        private String date; // dd/MM
        private double sales;
        private long orders;
        private long visitors;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySales {
        private String category;
        private double sales;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodSales {
        private String method; // COD, QR
        private double sales;
        private long count;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private String name;
        private String category;
        private int quantitySold;
        private double revenue;
        private String imageUrl;
    }
}
