package com.sabaidee.market.controller;

import com.sabaidee.market.dto.request.UpdateProfileRequest;
import com.sabaidee.market.dto.response.ApiResponse;
import com.sabaidee.market.model.User;
import com.sabaidee.market.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(Principal principal) {
        User user = userService.getProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(Principal principal,
                                                            @RequestBody UpdateProfileRequest request) {
        User user = userService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
