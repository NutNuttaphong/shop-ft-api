# 🛒 Sabaidee Market API

Sabaidee Market API คือ RESTful API สำหรับระบบร้านค้าออนไลน์ (E-Commerce) พัฒนาด้วย **Spring Boot 3.4.1** ร่วมกับฐานข้อมูล **MongoDB** และระบบรักษาความปลอดภัยด้วย **Spring Security + JWT**

---

## 🚀 ฟีเจอร์หลัก (Key Features)

1. **ระบบสมาชิกและสิทธิ์การใช้งาน (Authentication & Authorization)**
   - สมัครสมาชิก (Register) และเข้าสู่ระบบ (Login)
   - ความปลอดภัยด้วย **JSON Web Token (JWT)**
   - การแบ่งบทบาทผู้ใช้งาน (User Role) ได้แก่ `USER` และ `ADMIN`
2. **การจัดการบัญชีผู้ใช้ (User Profile Management)**
   - เรียกดูข้อมูลโปรไฟล์ส่วนตัว
   - อัปเดตข้อมูลผู้ใช้งาน (ชื่อแสดงผล, เบอร์โทรศัพท์, ที่อยู่)
3. **ระบบจัดการสินค้า (Product Catalog)**
   - เรียกดูรายการสินค้า ค้นหาด้วยชื่อ และคัดกรองตามประเภท (Category)
   - ดึงรายละเอียดสินค้าทีละรายการ
   - **[ADMIN]** เพิ่ม (Create), แก้ไข (Update) และลบ (Delete) สินค้า
   - **[ADMIN]** ระบบตรวจสอบสินค้าที่มีสต็อกต่ำกว่าที่กำหนด (Low-stock Alert)
4. **ระบบตะกร้าสินค้า (Shopping Cart)**
   - เพิ่มสินค้าเข้าตะกร้า พร้อมตรวจสอบระดับสต็อกสินค้าจริงแบบ Real-time
   - อัปเดตจำนวนสินค้า หรือลบสินค้าออกจากตะกร้า
   - ล้างข้อมูลตะกร้าสินค้าทั้งหมด
5. **ระบบสั่งซื้อสินค้า (Order & Checkout)**
   - ขั้นตอน Checkout เพื่อสั่งซื้อสินค้า (หักจำนวนสินค้าจากสต็อกอัตโนมัติ และล้างตะกร้าหลังสั่งสำเร็จ)
   - เรียกดูประวัติการสั่งซื้อของผู้ใช้งาน
   - **[ADMIN]** เรียกดูออเดอร์ทั้งหมดในระบบ และอัปเดตสถานะการจัดส่งสินค้า (`PENDING`, `PREPARING`, `SHIPPED`, `DELIVERED`, `CANCELLED`)
6. **ระบบคูปองและโปรโมชัน (Promotions)**
   - ระบบคูปองส่วนลดแบบระบุรหัส (Promo Code) รองรับทั้งแบบเปอร์เซ็นต์ (%) และลดแบบหักยอดคงที่ (Fixed Amount)
   - ตรวจสอบความถูกต้องและสิทธิ์การใช้งานคูปองส่วนลด
   - **[ADMIN]** จัดการข้อมูลคูปอง (เพิ่ม/แก้ไข/ลบ)
7. **แดชบอร์ดสถิติสำหรับผู้ดูแลระบบ (Admin Dashboard)**
   - **[ADMIN]** รวบรวมข้อมูลหลังบ้าน เช่น ยอดขายรวม (Total Revenue), จำนวนผู้ใช้ทั้งหมด, จำนวนออเดอร์, สินค้าขายดี, และการแจ้งเตือนสต็อกสินค้าต่ำ

---

## 🛠️ Stack เทคโนโลยี (Tech Stack)

- **Language:** Java 17+
- **Framework:** Spring Boot 3.4.1 (Spring Web, Spring Security)
- **Database:** MongoDB (Spring Data MongoDB)
- **Security:** JWT (JSON Web Tokens)
- **Documentation:** Springdoc OpenAPI (Swagger UI)
- **Utility:** Lombok, MapStruct (ถ้ามี)
- **Build Tool:** Maven

---

## 📁 โครงสร้างโฟลเดอร์ (Project Structure)

```text
src/main/java/com/sabaidee/market/
├── SabaideeMarketApplication.java  # คลาสหลักสำหรับรันแอปพลิเคชัน
├── config/                         # ไฟล์ตั้งค่าระบบ (CORS, Data Initializer, Security, Mongo)
├── controller/                     # REST Controllers (API Endpoints)
├── dto/                            # Data Transfer Objects (Request/Response)
│   ├── request/                    # คลาสข้อมูลที่รับจาก Client
│   └── response/                   # คลาสข้อมูลที่ส่งกลับไปยัง Client
├── exception/                      # Global Exception Handlers และ Custom Exceptions
├── model/                          # Entity/Document Models ของ MongoDB และ Enums
├── repository/                     # Spring Data MongoDB Repositories (Data Access)
├── security/                       # ตัวประมวลผลระบบความปลอดภัย (JWT Filters, Providers)
└── service/                        # Business Logic Services
```

---

## 📋 รายละเอียด API Endpoints

### 🔓 Public Endpoints (ไม่ต้องเข้าสู่ระบบ)
| Method | Endpoint | รายละเอียด |
|:---|:---|:---|
| POST | `/api/auth/register` | สมัครสมาชิกผู้ใช้งานใหม่ |
| POST | `/api/auth/login` | เข้าสู่ระบบ (รับ JWT Token สำหรับใช้งาน API อื่นๆ) |

### 🔐 Authenticated Endpoints (ต้องแนบ Authorization Header: `Bearer <token>`)
| Method | Endpoint | รายละเอียด |
|:---|:---|:---|
| **User Profile** | | |
| GET | `/api/users/profile` | ดูโปรไฟล์ตนเอง |
| PUT | `/api/users/profile` | แก้ไขข้อมูลโปรไฟล์ |
| **Product** | | |
| GET | `/api/products` | ดูรายการสินค้าทั้งหมด (รองรับ Query: `?search=`, `?category=`) |
| GET | `/api/products/{id}` | ดูรายละเอียดสินค้าเฉพาะตัว |
| **Shopping Cart** | | |
| GET | `/api/cart` | ดูตะกร้าสินค้า |
| POST | `/api/cart` | เพิ่มสินค้าลงในตะกร้า |
| PUT | `/api/cart/{productId}` | อัปเดตจำนวนสินค้าในตะกร้า (`?quantity=`) |
| DELETE | `/api/cart/{productId}` | ลบสินค้าออกจากตะกร้า |
| DELETE | `/api/cart` | ล้างตะกร้าสินค้าทั้งหมด |
| **Order & Checkout** | | |
| POST | `/api/orders/checkout` | สั่งซื้อสินค้าในตะกร้า (รองรับการใส่ Promo Code) |
| GET | `/api/orders/my` | ดูประวัติออเดอร์ของตนเอง |
| GET | `/api/orders/{id}` | ดูรายละเอียดออเดอร์เฉพาะตัว |
| **Promotion** | | |
| GET | `/api/promotions` | ดูรายการโปรโมชันทั้งหมด |
| GET | `/api/promotions/active` | ดูโปรโมชันที่กำลังใช้งานได้ในปัจจุบัน |
| GET | `/api/promotions/{id}` | ดูข้อมูลโปรโมชันเฉพาะตัว |
| GET | `/api/promotions/validate/{code}`| ตรวจสอบความถูกต้องของรหัสส่วนลด |

### 👑 Admin Endpoints (สิทธิ์ `ADMIN` เท่านั้น)
| Method | Endpoint | รายละเอียด |
|:---|:---|:---|
| **Product** | | |
| POST | `/api/products` | เพิ่มสินค้าใหม่เข้าระบบ |
| PUT | `/api/products/{id}` | แก้ไขรายละเอียดสินค้า |
| DELETE | `/api/products/{id}` | ลบสินค้าออกจากระบบ |
| GET | `/api/products/low-stock` | ดึงข้อมูลสินค้าที่สต็อกต่ำกว่ากำหนด (`?threshold=10`) |
| **Order** | | |
| GET | `/api/orders` | ดูรายการคำสั่งซื้อทั้งหมดในระบบ |
| PUT | `/api/orders/{id}/status` | อัปเดตสถานะออเดอร์ (`?status=PREPARING, SHIPPED, etc.`) |
| **Promotion** | | |
| POST | `/api/promotions` | สร้างโปรโมชัน/คูปองส่วนลดใหม่ |
| PUT | `/api/promotions/{id}` | อัปเดตข้อมูลคูปองส่วนลด |
| DELETE | `/api/promotions/{id}` | ลบคูปองส่วนลด |
| **Dashboard** | | |
| GET | `/api/dashboard` | เรียกดูข้อมูลสถิติภาพรวมแดชบอร์ด |

---

## 🧪 บัญชีสำหรับทดสอบระบบ (Seed Accounts)

ระบบมาพร้อมกับคลาส `DataInitializer` ซึ่งจะช่วยสร้างข้อมูลเริ่มต้นและบัญชีทดสอบในฐานข้อมูลอัตโนมัติเมื่อเปิดเซิร์ฟเวอร์ครั้งแรก:

| Username | Password | Role | รายละเอียด |
|:---|:---|:---|:---|
| `user@1234` | `user@1234` | **USER** | บัญชีผู้ซื้อปกติ |
| `admin@1234` | `admin@1234` | **ADMIN** | บัญชีผู้ดูแลระบบ |

---

## 🚀 ขั้นตอนการติดตั้งและเปิดใช้งาน (Setup & Run)

### 📌 สิ่งที่ต้องติดตั้งล่วงหน้า (Prerequisites)
1. **Java SDK 17 หรือใหม่กว่า**
2. **MongoDB Server** (รันที่โฮสต์เริ่มต้น `localhost:27017` โดยมีฐานข้อมูลชื่อ `sabaidee_market`)

### ⚡ สั่งรันโปรเจกต์
เปิด Terminal ในโฟลเดอร์โปรเจกต์ จากนั้นรันคำสั่งดังนี้:

```bash
# บน Windows
mvnw.cmd spring-boot:run

# บน macOS / Linux
./mvnw spring-boot:run
```

- **Base URL:** `http://localhost:8080`
- **API Documentation (Swagger UI):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
