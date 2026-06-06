package com.sabaidee.market.service;

import com.sabaidee.market.dto.request.LoginRequest;
import com.sabaidee.market.dto.request.RegisterRequest;
import com.sabaidee.market.dto.response.AuthResponse;
import com.sabaidee.market.exception.DuplicateResourceException;
import com.sabaidee.market.model.User;
import com.sabaidee.market.model.enums.UserRole;
import com.sabaidee.market.repository.UserRepository;
import com.sabaidee.market.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        log.info("ผู้ใช้ {} เข้าสู่ระบบสำเร็จ", user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .displayName(user.getDisplayName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("ชื่อผู้ใช้ \"" + request.getUsername() + "\" ถูกใช้งานแล้ว");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .displayName(request.getDisplayName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .cart(new ArrayList<>())
                .build();

        userRepository.save(user);
        log.info("สมัครสมาชิกสำเร็จ: {}", user.getUsername());

        String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .displayName(user.getDisplayName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }
}
