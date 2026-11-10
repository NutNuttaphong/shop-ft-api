package com.sabaidee.market.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabaidee.market.model.Order;
import com.sabaidee.market.model.User;
import com.sabaidee.market.model.enums.UserRole;
import com.sabaidee.market.repository.OrderRepository;
import com.sabaidee.market.repository.UserRepository;
import com.sabaidee.market.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityHardeningTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("User password must NOT be serialized into JSON (preventing password hash leakage)")
    void testUserPasswordIsNotSerialized() throws Exception {
        User user = User.builder()
                .id("u1")
                .username("user@1234")
                .password("$2a$10$superSecretHashValue")
                .role(UserRole.USER)
                .displayName("Test User")
                .build();

        String json = objectMapper.writeValueAsString(user);

        assertFalse(json.contains("superSecretHashValue"), "Password hash must not be present in JSON");
        assertFalse(json.contains("\"password\""), "Password field key must not be present in JSON");
    }

    @Test
    @DisplayName("Owner can access their own order")
    void testOwnerCanAccessOwnOrder() {
        Order order = Order.builder().id("ord-1").userId("u1").orderNo("OD100").build();
        User owner = User.builder().id("u1").username("user@1234").role(UserRole.USER).build();

        when(orderRepository.findById("ord-1")).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("user@1234")).thenReturn(Optional.of(owner));

        Order result = orderService.getOrderByIdAndValidateAccess("ord-1", "user@1234");
        assertNotNull(result);
        assertEquals("ord-1", result.getId());
    }

    @Test
    @DisplayName("Admin can access any user's order")
    void testAdminCanAccessAnyOrder() {
        Order order = Order.builder().id("ord-1").userId("u1").orderNo("OD100").build();
        User admin = User.builder().id("admin-id").username("admin@1234").role(UserRole.ADMIN).build();

        when(orderRepository.findById("ord-1")).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("admin@1234")).thenReturn(Optional.of(admin));

        Order result = orderService.getOrderByIdAndValidateAccess("ord-1", "admin@1234");
        assertNotNull(result);
        assertEquals("ord-1", result.getId());
    }

    @Test
    @DisplayName("Unauthorized user accessing another user's order must throw AccessDeniedException (IDOR Prevention)")
    void testUnauthorizedUserBlockedFromOrder() {
        Order order = Order.builder().id("ord-1").userId("u1").orderNo("OD100").build();
        User attacker = User.builder().id("u2").username("attacker@1234").role(UserRole.USER).build();

        when(orderRepository.findById("ord-1")).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("attacker@1234")).thenReturn(Optional.of(attacker));

        assertThrows(AccessDeniedException.class, () -> {
            orderService.getOrderByIdAndValidateAccess("ord-1", "attacker@1234");
        });
    }
}
