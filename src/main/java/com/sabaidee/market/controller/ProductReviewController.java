package com.sabaidee.market.controller;

import com.sabaidee.market.dto.request.ProductReviewRequest;
import com.sabaidee.market.dto.response.ApiResponse;
import com.sabaidee.market.exception.ResourceNotFoundException;
import com.sabaidee.market.model.Order;
import com.sabaidee.market.model.ProductReview;
import com.sabaidee.market.model.User;
import com.sabaidee.market.model.enums.OrderStatus;
import com.sabaidee.market.repository.OrderRepository;
import com.sabaidee.market.repository.ProductRepository;
import com.sabaidee.market.repository.ProductReviewRepository;
import com.sabaidee.market.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewRepository productReviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<List<ProductReview>>> getReviews(@PathVariable String productId) {
        List<ProductReview> reviews = productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<ProductReview>> createReview(
            @PathVariable String productId,
            @Valid @RequestBody ProductReviewRequest request,
            Principal principal) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้: " + principal.getName()));

        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบสินค้า: " + productId));

        // Verify if user has a DELIVERED order containing this product
        List<Order> userOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        boolean hasOrdered = userOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .flatMap(o -> o.getItems().stream())
                .anyMatch(item -> item.getProductId().equals(productId));

        if (!hasOrdered) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("คุณสามารถรีวิวสินค้าได้เฉพาะสินค้าที่เคยสั่งซื้อและได้รับสำเร็จแล้วเท่านั้น", 400));
        }

        ProductReview review = ProductReview.builder()
                .productId(productId)
                .userId(user.getId())
                .username(user.getUsername())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(Instant.now())
                .build();

        ProductReview savedReview = productReviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedReview));
    }
}
