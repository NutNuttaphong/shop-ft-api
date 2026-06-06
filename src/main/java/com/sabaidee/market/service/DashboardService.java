package com.sabaidee.market.service;

import com.sabaidee.market.dto.response.DashboardResponse;
import com.sabaidee.market.model.Order;
import com.sabaidee.market.model.Product;
import com.sabaidee.market.model.enums.UserRole;
import com.sabaidee.market.repository.OrderRepository;
import com.sabaidee.market.repository.ProductRepository;
import com.sabaidee.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    public DashboardResponse getDashboard() {
        List<Order> allOrders = orderRepository.findAllByOrderByCreatedAtDesc();
        List<Product> allProducts = productRepository.findAll();

        double totalSales = allOrders.stream()
                .mapToDouble(Order::getTotal)
                .sum();

        int totalStock = allProducts.stream()
                .mapToInt(Product::getStock)
                .sum();

        int lowStockCount = productRepository.findByStockLessThanEqual(10).size();
        long userCount = userRepository.countByRole(UserRole.USER);

        List<DashboardResponse.RecentOrder> recentOrders = allOrders.stream()
                .limit(5)
                .map(order -> DashboardResponse.RecentOrder.builder()
                        .orderId(order.getId())
                        .orderNo(order.getOrderNo())
                        .time(order.getCreatedAt() != null ? FORMATTER.format(order.getCreatedAt()) : "-")
                        .items(order.getItems().stream()
                                .map(item -> item.getName())
                                .collect(Collectors.joining(", ")))
                        .total(order.getTotal())
                        .status(order.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalSales(totalSales)
                .productCount(allProducts.size())
                .totalStock(totalStock)
                .lowStockCount(lowStockCount)
                .userCount((int) userCount)
                .orderCount(allOrders.size())
                .recentOrders(recentOrders)
                .build();
    }
}
