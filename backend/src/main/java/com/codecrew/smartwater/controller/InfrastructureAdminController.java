package com.codecrew.smartwater.controller;

import com.codecrew.smartwater.common.ApiResponse;
import com.codecrew.smartwater.dto.request.ApartmentRequest;
import com.codecrew.smartwater.dto.request.HouseholdRequest;
import com.codecrew.smartwater.dto.request.WaterMeterRequest;
import com.codecrew.smartwater.dto.response.ApartmentResponse;
import com.codecrew.smartwater.dto.response.HouseholdResponse;
import com.codecrew.smartwater.dto.response.WaterMeterResponse;
import com.codecrew.smartwater.service.ApartmentService;
import com.codecrew.smartwater.service.HouseholdService;
import com.codecrew.smartwater.service.WaterMeterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Infrastructure", description = "Endpoints for managing apartments, households, and meters (Admin only)")
@SecurityRequirement(name = "BearerAuth") // Enforces the JWT security scheme in Swagger
public class InfrastructureAdminController {

    private final ApartmentService apartmentService;
    private final HouseholdService householdService;
    private final WaterMeterService waterMeterService;

    public InfrastructureAdminController(ApartmentService apartmentService, HouseholdService householdService, WaterMeterService waterMeterService) {
        this.apartmentService = apartmentService;
        this.householdService = householdService;
        this.waterMeterService = waterMeterService;
    }

    // --- Apartment Endpoints ---

    @PostMapping("/apartments")
    @Operation(summary = "Register a new apartment complex")
    public ResponseEntity<ApiResponse<ApartmentResponse>> createApartment(@Valid @RequestBody ApartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Apartment created successfully", apartmentService.createApartment(request)));
    }

    @GetMapping("/apartments")
    @Operation(summary = "Get all apartments")
    public ResponseEntity<ApiResponse<List<ApartmentResponse>>> getAllApartments() {
        return ResponseEntity.ok(ApiResponse.success(apartmentService.getAllApartments()));
    }

    @GetMapping("/apartments/{id}")
    @Operation(summary = "Get apartment by ID")
    public ResponseEntity<ApiResponse<ApartmentResponse>> getApartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(apartmentService.getApartmentById(id)));
    }

    // --- Household Endpoints ---

    @PostMapping("/households")
    @Operation(summary = "Register a new household within an apartment")
    public ResponseEntity<ApiResponse<HouseholdResponse>> createHousehold(@Valid @RequestBody HouseholdRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Household created successfully", householdService.createHousehold(request)));
    }

    @GetMapping("/apartments/{apartmentId}/households")
    @Operation(summary = "Get all households for a specific apartment")
    public ResponseEntity<ApiResponse<List<HouseholdResponse>>> getHouseholdsByApartment(@PathVariable Long apartmentId) {
        return ResponseEntity.ok(ApiResponse.success(householdService.getHouseholdsByApartment(apartmentId)));
    }

    @PatchMapping("/households/{householdId}/assign-resident/{residentId}")
    @Operation(summary = "Assign a registered user (resident) to a household")
    public ResponseEntity<ApiResponse<HouseholdResponse>> assignResident(
            @PathVariable Long householdId,
            @PathVariable Long residentId) {
        return ResponseEntity.ok(ApiResponse.success("Resident assigned successfully",
                householdService.assignResident(householdId, residentId)));
    }

    // --- Water Meter Endpoints ---

    @PostMapping("/meters")
    @Operation(summary = "Configure a new water meter for a household")
    public ResponseEntity<ApiResponse<WaterMeterResponse>> createWaterMeter(@Valid @RequestBody WaterMeterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Water meter configured successfully", waterMeterService.createWaterMeter(request)));
    }
}