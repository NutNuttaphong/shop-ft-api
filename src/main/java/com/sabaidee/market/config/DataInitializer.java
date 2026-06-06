package com.sabaidee.market.config;

import com.sabaidee.market.model.Product;
import com.sabaidee.market.model.Promotion;
import com.sabaidee.market.model.User;
import com.sabaidee.market.model.enums.DiscountType;
import com.sabaidee.market.model.enums.UserRole;
import com.sabaidee.market.repository.ProductRepository;
import com.sabaidee.market.repository.PromotionRepository;
import com.sabaidee.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedProducts();
        seedPromotions();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            log.info("✅ Users already exist, skipping seed.");
            return;
        }

        List<User> users = List.of(
            User.builder()
                .username("user@1234")
                .password(passwordEncoder.encode("user@1234"))
                .role(UserRole.USER)
                .displayName("สมชาย รักดี (ลูกค้า)")
                .cart(new ArrayList<>())
                .build(),
            User.builder()
                .username("admin@1234")
                .password(passwordEncoder.encode("admin@1234"))
                .role(UserRole.ADMIN)
                .displayName("สมศรี จัดการระบบ (ผู้ดูแล)")
                .cart(new ArrayList<>())
                .build()
        );

        userRepository.saveAll(users);
        log.info("✅ Seeded {} users", users.size());
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            log.info("✅ Products already exist, skipping seed.");
            return;
        }

        List<Product> products = List.of(
            Product.builder()
                .id("prod-1")
                .name("ข้าวหอมมะลิแท้ 100% (5 กก.)")
                .price(220)
                .description("ข้าวหอมมะลิคุณภาพสูงจากทุ่งกุลาร้องไห้ บรรจุถุง 5 กิโลกรัม")
                .category("อาหารแห้งและเครื่องปรุง")
                .imageUrl("https://placehold.co/300x200/E8D5B7/333?text=Rice+5kg")
                .stock(50)
                .build(),
            Product.builder()
                .id("prod-2")
                .name("น้ำมันพืชตราดาวเด่น (1 ลิตร)")
                .price(55)
                .description("น้ำมันพืชคุณภาพ สำหรับทอดและผัด ขนาด 1 ลิตร")
                .category("อาหารแห้งและเครื่องปรุง")
                .imageUrl("https://placehold.co/300x200/F5E6A3/333?text=Oil+1L")
                .stock(120)
                .build(),
            Product.builder()
                .id("prod-3")
                .name("นมยูเอชที รสจืด (แพ็ค 6 กล่อง)")
                .price(78)
                .description("นมยูเอชทีรสจืด พร้อมดื่ม แพ็ค 6 กล่อง")
                .category("เครื่องดื่ม")
                .imageUrl("https://placehold.co/300x200/D4EFDF/333?text=Milk+6pk")
                .stock(80)
                .build(),
            Product.builder()
                .id("prod-4")
                .name("ไข่ไก่สด เบอร์ 2 (แผง 30 ฟอง)")
                .price(135)
                .description("ไข่ไก่สดคุณภาพ เบอร์ 2 บรรจุแผง 30 ฟอง")
                .category("อาหารสด")
                .imageUrl("https://placehold.co/300x200/FADBD8/333?text=Eggs+30")
                .stock(30)
                .build()
        );

        productRepository.saveAll(products);
        log.info("✅ Seeded {} products", products.size());
    }

    private void seedPromotions() {
        if (promotionRepository.count() > 0) {
            log.info("✅ Promotions already exist, skipping seed.");
            return;
        }

        List<Promotion> promotions = List.of(
            Promotion.builder()
                .id("promo-1")
                .code("SABAIDEE10")
                .name("สบายดี ลด 10%")
                .description("ส่วนลด 10% สำหรับสมาชิกใหม่ ไม่มีขั้นต่ำ")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .minPurchase(0)
                .isActive(true)
                .startDate("2024-01-01")
                .endDate("2025-12-31")
                .build(),
            Promotion.builder()
                .id("promo-2")
                .code("MIDYEAR50")
                .name("กลางปีลด 50 บาท")
                .description("ส่วนลด 50 บาท เมื่อซื้อครบ 300 บาท")
                .discountType(DiscountType.FIXED)
                .discountValue(50)
                .minPurchase(300)
                .isActive(true)
                .startDate("2024-06-01")
                .endDate("2024-08-31")
                .build(),
            Promotion.builder()
                .id("promo-3")
                .code("HEALTHY20")
                .name("สุขภาพดี ลด 20%")
                .description("ส่วนลด 20% สำหรับหมวดอาหารสด เมื่อซื้อครบ 200 บาท")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(20)
                .minPurchase(200)
                .isActive(false)
                .startDate("2024-03-01")
                .endDate("2024-05-31")
                .build()
        );

        promotionRepository.saveAll(promotions);
        log.info("✅ Seeded {} promotions", promotions.size());
    }
}
