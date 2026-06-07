package com.sabaidee.market.service;

import com.sabaidee.market.dto.request.CartItemRequest;
import com.sabaidee.market.exception.InsufficientStockException;
import com.sabaidee.market.exception.ResourceNotFoundException;
import com.sabaidee.market.model.CartItem;
import com.sabaidee.market.model.Product;
import com.sabaidee.market.model.User;
import com.sabaidee.market.repository.ProductRepository;
import com.sabaidee.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<CartItem> getCart(String username) {
        User user = findUser(username);
        return user.getCart();
    }

    public List<CartItem> addToCart(String username, CartItemRequest request) {
        User user = findUser(username);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบสินค้ารหัส: " + request.getProductId()));

        // Check if product already in cart with same variant
        CartItem existingItem = user.getCart().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()) && 
                        java.util.Objects.equals(item.getVariant(), request.getVariantName()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStock()) {
                throw new InsufficientStockException(
                        "สินค้า \"" + product.getName() + "\" มีสต็อกเหลือ " + product.getStock() + " ชิ้น");
            }
            existingItem.setQuantity(newQuantity);
        } else {
            if (request.getQuantity() > product.getStock()) {
                throw new InsufficientStockException(
                        "สินค้า \"" + product.getName() + "\" มีสต็อกเหลือ " + product.getStock() + " ชิ้น");
            }
            
            String displayName = product.getName();
            if (request.getVariantName() != null && !request.getVariantName().isEmpty()) {
                displayName += " (" + request.getVariantName() + ")";
            }

            CartItem newItem = CartItem.builder()
                    .productId(product.getId())
                    .name(displayName)
                    .price(product.getPrice() + request.getPriceAdjustment())
                    .quantity(request.getQuantity())
                    .imageUrl(product.getImageUrl())
                    .stock(product.getStock())
                    .category(product.getCategory())
                    .variant(request.getVariantName())
                    .build();
            user.getCart().add(newItem);
        }

        userRepository.save(user);
        log.info("เพิ่มสินค้า {} ({}) ลงตะกร้าของ {}", product.getName(), request.getVariantName(), username);
        return user.getCart();
    }

    public List<CartItem> updateCartItemQuantity(String username, String productId, int quantity, String variant) {
        User user = findUser(username);

        if (quantity <= 0) {
            user.getCart().removeIf(item -> item.getProductId().equals(productId) && 
                    java.util.Objects.equals(item.getVariant(), variant));
        } else {
            CartItem item = user.getCart().stream()
                    .filter(i -> i.getProductId().equals(productId) && 
                            java.util.Objects.equals(i.getVariant(), variant))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("ไม่พบสินค้านี้ในตะกร้า"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("ไม่พบสินค้ารหัส: " + productId));

            if (quantity > product.getStock()) {
                throw new InsufficientStockException(
                        "สินค้า \"" + product.getName() + "\" มีสต็อกเหลือ " + product.getStock() + " ชิ้น");
            }
            item.setQuantity(quantity);
        }

        userRepository.save(user);
        return user.getCart();
    }

    public List<CartItem> removeFromCart(String username, String productId, String variant) {
        User user = findUser(username);
        user.getCart().removeIf(item -> item.getProductId().equals(productId) && 
                java.util.Objects.equals(item.getVariant(), variant));
        userRepository.save(user);
        log.info("ลบสินค้า {} ({}) ออกจากตะกร้าของ {}", productId, variant, username);
        return user.getCart();
    }

    public void clearCart(String username) {
        User user = findUser(username);
        user.getCart().clear();
        userRepository.save(user);
        log.info("ล้างตะกร้าของ {}", username);
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้: " + username));
    }
}
