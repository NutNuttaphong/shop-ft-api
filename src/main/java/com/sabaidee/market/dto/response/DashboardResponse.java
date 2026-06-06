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
public class DashboardResponse {
    private double totalSales;
    private int productCount;
    private int totalStock;
    private int lowStockCount;
    private int userCount;
    private int orderCount;
    private List<RecentOrder> recentOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentOrder {
        private String orderId;
        private String orderNo;
        private String time;
        private String items;
        private double total;
        private String status;
    }
}
