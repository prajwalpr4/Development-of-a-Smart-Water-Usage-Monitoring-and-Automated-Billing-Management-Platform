package com.codecrew.smartwater.controller;

import com.codecrew.smartwater.common.ApiResponse;
import com.codecrew.smartwater.dto.response.UserProfileResponse;
import com.codecrew.smartwater.dto.response.WaterUsageLogResponse;
import com.codecrew.smartwater.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/resident")
@Tag(name = "Resident Portal", description = "Endpoints accessible by residents to view profiles and usage history")
@SecurityRequirement(name = "BearerAuth")
public class ResidentController {

    private final UserService userService;

    public ResidentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current resident's profile and flat details")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserProfile(authentication.getName())));
    }

    @GetMapping("/usage")
    @Operation(summary = "Get water usage logs for the logged-in resident within a date range")
    public ResponseEntity<ApiResponse<List<WaterUsageLogResponse>>> getUsageHistory(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.getResidentUsageHistory(authentication.getName(), startDate, endDate)));
    }
}