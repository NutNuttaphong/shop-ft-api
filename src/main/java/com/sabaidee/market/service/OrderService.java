package com.sabaidee.market.service;

import com.sabaidee.market.dto.request.CheckoutRequest;
import com.sabaidee.market.exception.InsufficientStockException;
import com.sabaidee.market.exception.ResourceNotFoundException;
import com.sabaidee.market.model.*;
import com.sabaidee.market.model.enums.OrderStatus;
import com.sabaidee.market.repository.OrderRepository;
import com.sabaidee.market.repository.ProductRepository;
import com.sabaidee.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public Order checkout(String username, CheckoutRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้: " + username));

        if (user.getCart() == null || user.getCart().isEmpty()) {
            throw new IllegalStateException("ตะกร้าสินค้าว่างเปล่า ไม่สามารถสั่งซื้อได้");
        }

        // Validate stock and build order items
        List<OrderItem> orderItems = user.getCart().stream()
                .map(cartItem -> {
                    Product product = productRepository.findById(cartItem.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("ไม่พบสินค้า: " + cartItem.getName()));

                    if (product.getStock() < cartItem.getQuantity()) {
                        throw new InsufficientStockException(
                                "สินค้า \"" + product.getName() + "\" มีสต็อกเหลือ " + product.getStock() + " ชิ้น");
                    }

                    // Deduct stock
                    product.setStock(product.getStock() - cartItem.getQuantity());
                    productRepository.save(product);

                    return OrderItem.builder()
                            .productId(cartItem.getProductId())
                            .name(cartItem.getName())
                            .price(cartItem.getPrice())
                            .quantity(cartItem.getQuantity())
                            .imageUrl(cartItem.getImageUrl())
                            .category(cartItem.getCategory())
                            .build();
                })
                .collect(Collectors.toList());

        double total = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .orderNo(generateOrderNo())
                .userId(user.getId())
                .items(orderItems)
                .total(total)
                .customer(CustomerInfo.builder()
                        .name(request.getCustomerName())
                        .phone(request.getCustomerPhone())
                        .address(request.getCustomerAddress())
                        .build())
                .paymentMethod(request.getPaymentMethod())
                .slipUploaded(request.isSlipUploaded())
                .slipName(request.getSlipName())
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        // Clear user cart
        user.getCart().clear();
        userRepository.save(user);

        log.info("สร้างออเดอร์ {} สำเร็จ สำหรับผู้ใช้ {}", order.getOrderNo(), username);
        return order;
    }

    public List<Order> getMyOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้: " + username));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบออเดอร์รหัส: " + id));
    }

    public Order updateOrderStatus(String id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        log.info("อัพเดทสถานะออเดอร์ {} เป็น {}", order.getOrderNo(), status);
        return orderRepository.save(order);
    }

    private String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomNum = new Random().nextInt(9000) + 1000; // 1000-9999
        return "INV-" + date + "-" + randomNum;
    }
}
