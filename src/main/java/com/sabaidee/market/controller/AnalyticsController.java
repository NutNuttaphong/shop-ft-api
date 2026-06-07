package com.sabaidee.market.controller;

import com.sabaidee.market.dto.response.ApiResponse;
import com.sabaidee.market.dto.response.AnalyticsResponse;
import com.sabaidee.market.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping("/visit")
    public ResponseEntity<ApiResponse<String>> logVisit(
            @RequestBody VisitRequest request,
            HttpServletRequest httpServletRequest,
            Principal principal) {
        
        String ipAddress = httpServletRequest.getRemoteAddr();
        String username = principal != null ? principal.getName() : "anonymous";
        
        analyticsService.logVisit(request.getSessionId(), request.getPageUrl(), username, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("บันทึกข้อมูลทราฟฟิกแล้ว"));
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getOverview() {
        AnalyticsResponse response = analyticsService.getOverview();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Data
    public static class VisitRequest {
        private String sessionId;
        private String pageUrl;
    }
}
