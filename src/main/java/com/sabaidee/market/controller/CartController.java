package com.sabaidee.market.controller;

import com.sabaidee.market.dto.request.CartItemRequest;
import com.sabaidee.market.dto.response.ApiResponse;
import com.sabaidee.market.model.CartItem;
import com.sabaidee.market.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItem>>> getCart(Principal principal) {
        List<CartItem> cart = cartService.getCart(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<CartItem>>> addToCart(Principal principal,
                                                                  @Valid @RequestBody CartItemRequest request) {
        List<CartItem> cart = cartService.addToCart(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<List<CartItem>>> updateCartItem(Principal principal,
                                                                       @PathVariable String productId,
                                                                       @RequestParam int quantity) {
        List<CartItem> cart = cartService.updateCartItemQuantity(principal.getName(), productId, quantity);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<List<CartItem>>> removeFromCart(Principal principal,
                                                                       @PathVariable String productId) {
        List<CartItem> cart = cartService.removeFromCart(principal.getName(), productId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("ล้างตะกร้าสำเร็จ"));
    }
}
