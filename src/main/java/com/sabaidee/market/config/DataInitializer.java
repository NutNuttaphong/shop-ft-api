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
        // Clear existing products to enforce marketplace seeding refresh
        productRepository.deleteAll();

        List<Product> products = List.of(
            Product.builder()
                .id("prod-1")
                .name("iPhone 15 Pro Max (256GB) เครื่องศูนย์ไทย")
                .price(41900)
                .description("สมาร์ทโฟนระดับเรือธงพร้อมชิป A17 Pro กล้องซูมออปติคัล 5 เท่า และตัวเครื่องไทเทเนียมสุดแกร่ง หน้าจอ Super Retina XDR 6.7 นิ้ว")
                .category("อิเล็กทรอนิกส์")
                .imageUrl("https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(45)
                .build(),
            Product.builder()
                .id("prod-2")
                .name("หูฟังไร้สายบูลทูธตัดเสียงรบกวน ANC Wireless")
                .price(1290)
                .description("หูฟังแบบครอบหูตัดเสียงรบกวนอัจฉริยะ แบตเตอรี่อึดทนนาน 40 ชั่วโมง พลังเสียงสเตอริโอเบสแน่น ดีไซน์พับเก็บได้พกพาสะดวก")
                .category("อิเล็กทรอนิกส์")
                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(120)
                .build(),
            Product.builder()
                .id("prod-3")
                .name("คีย์บอร์ดกลไกไร้สาย Mechanical Keyboard RGB")
                .price(2590)
                .description("แป้นพิมพ์กลไกขนาด 75% เชื่อมต่อได้ 3 โหมด (Bluetooth / 2.4G / Type-C) พร้อมไฟ RGB ปรับแต่งสวิตช์แบบ Hot-swappable")
                .category("อิเล็กทรอนิกส์")
                .imageUrl("https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(68)
                .build(),
            Product.builder()
                .id("prod-4")
                .name("เสื้อยืดคอตตอน 100% สไตล์เกาหลี Oversized")
                .price(290)
                .description("เสื้อยืดทรงหลวมคอกลม ผลิตจากผ้าฝ้ายคอตตอนธรรมชาติ 100% เนื้อผ้านุ่มระบายอากาศได้ดี ใส่สบายได้ทุกวันสไตล์มินิมอลเกาหลี")
                .category("เสื้อผ้าและแฟชั่น")
                .imageUrl("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(250)
                .build(),
            Product.builder()
                .id("prod-5")
                .name("กางเกงยีนส์ยืดทรงขากระบอกตรง Denim Jeans")
                .price(690)
                .description("กางเกงยีนส์ทรงขากระบอกตรงเนื้อผ้าเดนิมยืดหยุ่นพิเศษ สวมใส่สบายกระชับทรงสวยคลาสสิก เข้ากับเสื้อผ้าได้ทุกสไตล์")
                .category("เสื้อผ้าและแฟชั่น")
                .imageUrl("https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(150)
                .build(),
            Product.builder()
                .id("prod-6")
                .name("รองเท้าผ้าใบสตรีทแฟชั่นรุ่น Retro Run")
                .price(1890)
                .description("รองเท้าผ้าใบสไตล์เรโทรสวมใส่เบาสบายเท้าพร้อมพื้นซัพพอร์ตแรงกระแทก พื้นยางยึดเกาะดีเยี่ยม เหมาะสำหรับใส่เที่ยวและออกกำลังกายเบาๆ")
                .category("เสื้อผ้าและแฟชั่น")
                .imageUrl("https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(85)
                .build(),
            Product.builder()
                .id("prod-7")
                .name("หม้อทอดไร้น้ำมันอัจฉริยะ Smart Air Fryer 4.5L")
                .price(1590)
                .description("หม้อทอดไร้น้ำมันความจุ 4.5 ลิตร หน้าจอสัมผัสอัจฉริยะพร้อมเมนูทำอาหารอัตโนมัติ ลมร้อนหมุนเวียน 360 องศาเพื่ออาหารเพื่อสุขภาพ")
                .category("เครื่องใช้ในบ้าน")
                .imageUrl("https://images.unsplash.com/photo-1621972750749-0fbb1abb7736?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(95)
                .build(),
            Product.builder()
                .id("prod-8")
                .name("หุ่นยนต์ดูดฝุ่นและถูพื้นอัจฉริยะ Robot Vacuum")
                .price(6990)
                .description("หุ่นยนต์ทำความสะอาดบ้านอัจฉริยะ นำทางด้วยระบบเลเซอร์ Lidar หลบสิ่งกีดขวางได้อย่างแม่นยำ พร้อมแรงดูดสูงและระบบควบคุมการถูเปียก")
                .category("เครื่องใช้ในบ้าน")
                .imageUrl("https://images.unsplash.com/photo-1518310383802-640c2de311b2?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(35)
                .build(),
            Product.builder()
                .id("prod-9")
                .name("พัดลมตั้งโต๊ะมินิมอลสไตล์ญี่ปุ่นปรับแรงลมได้")
                .price(450)
                .description("พัดลมตั้งโต๊ะขนาดกะทัดรัด ดีไซน์ขาวมินิมอล ปรับความแรงลมได้ 3 ระดับ ทำงานเงียบสนิทประหยัดไฟ เหมาะสำหรับโต๊ะทำงาน")
                .category("เครื่องใช้ในบ้าน")
                .imageUrl("https://images.unsplash.com/photo-1618944847828-82e943c3dba7?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(180)
                .build(),
            Product.builder()
                .id("prod-10")
                .name("เซรั่มบำรุงผิวหน้าเข้มข้น Hyaluronic Glow Serum")
                .price(490)
                .description("เซรั่มบำรุงล้ำลึกช่วยให้ผิวชุ่มชื้น เปล่งปลั่งกระจ่างใส กระชับรูขุมขน และลดเลือนริ้วรอยด้วยสารสกัดธรรมชาติสูตรอ่อนโยน")
                .category("สุขภาพและความงาม")
                .imageUrl("https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(200)
                .build(),
            Product.builder()
                .id("prod-11")
                .name("ครีมกันแดดเนื้อบางเบาพิเศษ SPF50+ PA++++")
                .price(350)
                .description("กันแดดเนื้อน้ำนมสูตรบางเบาพิเศษ ซึมซาบไวไม่เหนียวเหนอะหนะ ไม่ทิ้งคราบขาว ควบคุมความมันยาวนานตลอดวัน ปกป้องรังสี UVA/UVB ครบครัน")
                .category("สุขภาพและความงาม")
                .imageUrl("https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(320)
                .build(),
            Product.builder()
                .id("prod-12")
                .name("ข้าวหอมมะลิแท้ 100% จากทุ่งกุลาร้องไห้ (5 กก.)")
                .price(240)
                .description("ข้าวหอมมะลิคัดพิเศษเมล็ดเรียวยาว หอม นุ่ม อร่อยทุกคำ เมล็ดเรียวสวยหุงขึ้นหม้อมาตรฐานส่งออกระดับพรีเมียม")
                .category("ซูเปอร์มาร์เก็ตและอาหาร")
                .imageUrl("https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(110)
                .build(),
            Product.builder()
                .id("prod-13")
                .name("น้ำมันมะพร้าวสกัดเย็นออร์แกนิก 100% (500ml)")
                .price(320)
                .description("น้ำมันมะพร้าวสกัดเย็นบริสุทธิ์ 100% ปราศจากสารเคมี อุดมด้วยกรดลอริกดีต่อสุขภาพ ใช้บำรุงผิวพรรณ เส้นผม หรือรับประทานเพื่อการควบคุมน้ำหนัก")
                .category("ซูเปอร์มาร์เก็ตและอาหาร")
                .imageUrl("https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?auto=format&fit=crop&w=300&h=200&q=80")
                .stock(90)
                .build()
        );

        productRepository.saveAll(products);
        log.info("✅ Seeded {} products", products.size());
    }

    private void seedPromotions() {
        // Clear promotions to enforce brand refresh
        promotionRepository.deleteAll();

        List<Promotion> promotions = List.of(
            Promotion.builder()
                .id("promo-1")
                .code("FRIST10")
                .name("FRIST ลด 10%")
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
                .code("TECH15")
                .name("ไอทีเซฟ 15%")
                .description("ส่วนลด 15% สำหรับหมวดอิเล็กทรอนิกส์ เมื่อซื้อครบ 5,000 บาท")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(15)
                .minPurchase(5000)
                .isActive(true)
                .startDate("2026-01-01")
                .endDate("2026-12-31")
                .build()
        );

        promotionRepository.saveAll(promotions);
        log.info("✅ Seeded {} promotions", promotions.size());
    }
}
