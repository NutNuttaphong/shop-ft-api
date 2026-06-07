package com.sabaidee.market.service;

import com.sabaidee.market.dto.request.CheckoutRequest;
import com.sabaidee.market.exception.InsufficientStockException;
import com.sabaidee.market.exception.ResourceNotFoundException;
import com.sabaidee.market.model.*;
import com.sabaidee.market.model.enums.OrderStatus;
import com.sabaidee.market.repository.OrderRepository;
import com.sabaidee.market.repository.ProductRepository;
import com.sabaidee.market.repository.UserRepository;
import com.sabaidee.market.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final NotificationRepository notificationRepository;
    private final SseNotificationService sseNotificationService;

    public Order checkout(String username, CheckoutRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้: " + username));

        List<CartItem> checkoutItems;
        boolean isBuyNow = request.getItems() != null && !request.getItems().isEmpty();
        
        if (isBuyNow) {
            checkoutItems = request.getItems();
        } else {
            if (user.getCart() == null || user.getCart().isEmpty()) {
                throw new IllegalStateException("ตะกร้าสินค้าว่างเปล่า ไม่สามารถสั่งซื้อได้");
            }
            checkoutItems = user.getCart();
        }

        // Validate stock and build order items
        List<OrderItem> orderItems = checkoutItems.stream()
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
                            .variant(cartItem.getVariant())
                            .build();
                })
                .collect(Collectors.toList());

        double total = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        double finalTotal = total - request.getDiscount() - request.getCoinsUsed();
        if (finalTotal < 0) finalTotal = 0;

        Order order = Order.builder()
                .orderNo(generateOrderNo())
                .userId(user.getId())
                .items(orderItems)
                .total(finalTotal)
                .discount(request.getDiscount())
                .coinsUsed(request.getCoinsUsed())
                .promoCode(request.getPromoCode())
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

        Order savedOrder = orderRepository.save(order);

        // Clear user cart only if not Buy Now
        if (!isBuyNow) {
            user.getCart().clear();
            userRepository.save(user);
        }

        createAndSendNotification(
                user.getId(),
                username,
                "สั่งซื้อสินค้าสำเร็จ!",
                "ได้รับคำสั่งซื้อเลขที่ " + savedOrder.getOrderNo() + " เรียบร้อยแล้วค่ะ",
                savedOrder.getId()
        );

        log.info("สร้างออเดอร์ {} สำเร็จ สำหรับผู้ใช้ {}", savedOrder.getOrderNo(), username);
        return savedOrder;
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

        if (status == OrderStatus.SHIPPED) {
            if (order.getCarrier() == null || order.getCarrier().isEmpty()) {
                String[] carriers = {"Kerry Express", "Flash Express", "J&T Express", "Thailand Post"};
                String carrier = carriers[new Random().nextInt(carriers.length)];
                order.setCarrier(carrier);
                
                String trackingNo = "TH" + (new Random().nextLong(900000000000L) + 100000000000L);
                order.setTrackingNumber(trackingNo);
            }
        }

        Order savedOrder = orderRepository.save(order);

        // Get user details to push notification
        User user = userRepository.findById(savedOrder.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้สำหรับคำสั่งซื้อนี้"));

        String title = "";
        String message = "";
        switch (status) {
            case PREPARING:
                title = "ร้านค้ากำลังเตรียมจัดส่ง";
                message = "คำสั่งซื้อเลขที่ " + savedOrder.getOrderNo() + " กำลังอยู่ในขั้นตอนเตรียมจัดส่งสินค้าค่ะ";
                break;
            case SHIPPED:
                title = "สินค้าจัดส่งแล้ว!";
                message = "คำสั่งซื้อเลขที่ " + savedOrder.getOrderNo() + " ได้จัดส่งผ่าน " + savedOrder.getCarrier() + " เลขพัสดุ: " + savedOrder.getTrackingNumber();
                break;
            case DELIVERED:
                title = "จัดส่งสำเร็จ";
                message = "คำสั่งซื้อเลขที่ " + savedOrder.getOrderNo() + " นำส่งถึงผู้รับเรียบร้อยแล้ว ขอบคุณที่สั่งซื้อสินค้ากับเราค่ะ";
                break;
            case CANCELLED:
                title = "คำสั่งซื้อยกเลิก";
                message = "คำสั่งซื้อเลขที่ " + savedOrder.getOrderNo() + " ได้ถูกยกเลิกแล้วค่ะ";
                break;
            default:
                title = "อัปเดตคำสั่งซื้อ";
                message = "คำสั่งซื้อเลขที่ " + savedOrder.getOrderNo() + " ได้เปลี่ยนสถานะเป็น " + status.name();
                break;
        }

        createAndSendNotification(user.getId(), user.getUsername(), title, message, savedOrder.getId());

        log.info("อัพเดทสถานะออเดอร์ {} เป็น {}", savedOrder.getOrderNo(), status);
        return savedOrder;
    }

    public Order requestReturn(String id, String username, com.sabaidee.market.dto.request.OrderReturnRequest request) {
        Order order = getOrderById(id);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้: " + username));

        if (!order.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("คุณไม่มีสิทธิ์ขอคืนเงินสำหรับออเดอร์นี้");
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("สามารถขอคืนเงินได้เฉพาะคำสั่งซื้อที่จัดส่งสำเร็จแล้วเท่านั้น");
        }

        order.setStatus(OrderStatus.RETURN_REQUESTED);
        order.setReturnReason(request.getReturnReason());
        order.setReturnDescription(request.getReturnDescription());

        Order savedOrder = orderRepository.save(order);

        // Notify user
        createAndSendNotification(
                user.getId(),
                user.getUsername(),
                "ยื่นคำขอคืนเงินสำเร็จ",
                "ระบบได้รับคำร้องขอคืนเงินสำหรับออเดอร์ " + savedOrder.getOrderNo() + " เรียบร้อยแล้วค่ะ",
                savedOrder.getId()
        );

        // Notify admin
        userRepository.findByUsername("admin").ifPresent(admin -> {
            createAndSendNotification(
                    admin.getId(),
                    admin.getUsername(),
                    "มีคำขอคืนสินค้า/คืนเงินใหม่",
                    "ผู้ใช้ " + username + " ขอคืนเงินสำหรับออเดอร์ " + savedOrder.getOrderNo(),
                    savedOrder.getId()
            );
        });

        log.info("ผู้ใช้ {} ส่งคำร้องขอคืนเงินสำหรับออเดอร์ {}", username, savedOrder.getOrderNo());
        return savedOrder;
    }

    public Order resolveReturn(String id, boolean approve) {
        Order order = getOrderById(id);

        if (order.getStatus() != OrderStatus.RETURN_REQUESTED) {
            throw new IllegalStateException("ออเดอร์นี้ไม่ได้อยู่ในสถานะขอคืนเงิน");
        }

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้สำหรับคำสั่งซื้อนี้"));

        if (approve) {
            order.setStatus(OrderStatus.RETURN_REFUNDED);
            
            // Refund stock
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    productRepository.findById(item.getProductId()).ifPresent(prod -> {
                        prod.setStock(prod.getStock() + item.getQuantity());
                        productRepository.save(prod);
                    });
                }
            }

            // Note: coinsUsed will be refunded on frontend side via local storage update when customer views status.
            createAndSendNotification(
                    user.getId(),
                    user.getUsername(),
                    "อนุมัติคืนเงินสำเร็จ!",
                    "คำร้องคืนเงินสำหรับออเดอร์ " + order.getOrderNo() + " ได้รับการอนุมัติแล้วค่ะ" + 
                    (order.getCoinsUsed() > 0 ? " ได้คืน First Shop Coins จำนวน " + (int)order.getCoinsUsed() + " Coins แล้ว" : ""),
                    order.getId()
            );
        } else {
            // Revert back to DELIVERED, allowing them to open a dispute if needed
            order.setStatus(OrderStatus.DELIVERED);
            createAndSendNotification(
                    user.getId(),
                    user.getUsername(),
                    "ปฏิเสธคำขอคืนเงิน",
                    "คำร้องคืนเงินสำหรับออเดอร์ " + order.getOrderNo() + " ถูกปฏิเสธ หากคุณมีข้อสงสัยสามารถเปิดข้อพิพาทหรือแชทคุยกับเราได้ค่ะ",
                    order.getId()
            );
        }

        Order savedOrder = orderRepository.save(order);
        log.info("แอดมินจัดการคำร้องขอคืนเงินสำหรับออเดอร์ {} ผลลัพธ์: {}", savedOrder.getOrderNo(), approve ? "อนุมัติ" : "ปฏิเสธ");
        return savedOrder;
    }

    public Order openDispute(String id, String username, com.sabaidee.market.dto.request.OrderDisputeRequest request) {
        Order order = getOrderById(id);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้: " + username));

        if (!order.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("คุณไม่มีสิทธิ์เข้าถึงออเดอร์นี้");
        }

        order.setDisputeOpened(true);
        order.setDisputeReason(request.getDisputeReason());
        order.setDisputeStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        // Notify user
        createAndSendNotification(
                user.getId(),
                user.getUsername(),
                "เปิดข้อพิพาทสำเร็จ",
                "ข้อพิพาทสำหรับคำสั่งซื้อ " + savedOrder.getOrderNo() + " ถูกบันทึกแล้วและอยู่ระหว่างเจ้าหน้าที่ตรวจสอบ",
                savedOrder.getId()
        );

        // Notify admin
        userRepository.findByUsername("admin").ifPresent(admin -> {
            createAndSendNotification(
                    admin.getId(),
                    admin.getUsername(),
                    "มีเคสข้อพิพาทใหม่",
                    "ออเดอร์ " + savedOrder.getOrderNo() + " เปิดข้อพิพาทโดย " + username + ": " + request.getDisputeReason(),
                    savedOrder.getId()
            );
        });

        log.info("ผู้ใช้ {} เปิดข้อพิพาทสำหรับออเดอร์ {}", username, savedOrder.getOrderNo());
        return savedOrder;
    }

    public Order resolveDispute(String id) {
        Order order = getOrderById(id);

        if (!order.isDisputeOpened()) {
            throw new IllegalStateException("ออเดอร์นี้ไม่มีการเปิดข้อพิพาท");
        }

        order.setDisputeOpened(false);
        order.setDisputeStatus("RESOLVED");

        Order savedOrder = orderRepository.save(order);

        User user = userRepository.findById(savedOrder.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้สำหรับคำสั่งซื้อนี้"));

        createAndSendNotification(
                user.getId(),
                user.getUsername(),
                "ข้อพิพาทได้รับการแก้ไขแล้ว",
                "ข้อพิพาทสำหรับออเดอร์ " + savedOrder.getOrderNo() + " ได้รับการแก้ปัญหายุติเคสเรียบร้อยแล้วค่ะ",
                savedOrder.getId()
        );

        log.info("ปิดข้อพิพาทออเดอร์ {} เรียบร้อย", savedOrder.getOrderNo());
        return savedOrder;
    }

    private void createAndSendNotification(String userId, String username, String title, String message, String orderId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type("ORDER_STATUS")
                .orderId(orderId)
                .read(false)
                .build();
        Notification savedNotification = notificationRepository.save(notification);
        sseNotificationService.sendNotification(username, savedNotification);
    }

    private String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomNum = new Random().nextInt(9000) + 1000; // 1000-9999
        return "INV-" + date + "-" + randomNum;
    }
}
