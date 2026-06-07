package com.sabaidee.market.controller;

import com.sabaidee.market.dto.request.CheckoutRequest;
import com.sabaidee.market.dto.response.ApiResponse;
import com.sabaidee.market.model.Order;
import com.sabaidee.market.model.enums.OrderStatus;
import com.sabaidee.market.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Order>> checkout(Principal principal,
                                                        @Valid @RequestBody CheckoutRequest request) {
        Order order = orderService.checkout(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(order));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Order>>> getMyOrders(Principal principal) {
        List<Order> orders = orderService.getMyOrders(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(@PathVariable String id,
                                                                  @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<ApiResponse<Order>> requestReturn(
            @PathVariable String id,
            @Valid @RequestBody com.sabaidee.market.dto.request.OrderReturnRequest request,
            Principal principal) {
        Order order = orderService.requestReturn(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{id}/resolve-return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> resolveReturn(
            @PathVariable String id,
            @RequestParam boolean approve) {
        Order order = orderService.resolveReturn(id, approve);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{id}/dispute")
    public ResponseEntity<ApiResponse<Order>> openDispute(
            @PathVariable String id,
            @Valid @RequestBody com.sabaidee.market.dto.request.OrderDisputeRequest request,
            Principal principal) {
        Order order = orderService.openDispute(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{id}/resolve-dispute")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> resolveDispute(@PathVariable String id) {
        Order order = orderService.resolveDispute(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
}
