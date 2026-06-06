package com.sabaidee.market.service;

import com.sabaidee.market.dto.request.UpdateProfileRequest;
import com.sabaidee.market.exception.ResourceNotFoundException;
import com.sabaidee.market.model.User;
import com.sabaidee.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้: " + username));
    }

    public User updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้: " + username));

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        log.info("อัพเดทโปรไฟล์ผู้ใช้: {}", username);
        return userRepository.save(user);
    }
}
