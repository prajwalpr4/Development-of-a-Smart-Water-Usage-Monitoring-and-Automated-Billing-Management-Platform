package com.codecrew.smartwater.controller;

import com.codecrew.smartwater.common.ApiResponse;
import com.codecrew.smartwater.dto.request.HistoricalSimulationRequest;
import com.codecrew.smartwater.dto.request.MeterReadingRequest;
import com.codecrew.smartwater.dto.response.WaterUsageLogResponse;
import com.codecrew.smartwater.model.entity.WaterUsageLog;
import com.codecrew.smartwater.service.VirtualMeterSimulatorService;
import com.codecrew.smartwater.service.WaterUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/usage")
@Tag(name = "Admin Water Usage & Simulation", description = "Endpoints for logging water consumption and triggering the virtual simulator")
@SecurityRequirement(name = "BearerAuth")
public class WaterUsageAdminController {

    private final WaterUsageService waterUsageService;
    private final VirtualMeterSimulatorService simulatorService;

    public WaterUsageAdminController(WaterUsageService waterUsageService, VirtualMeterSimulatorService simulatorService) {
        this.waterUsageService = waterUsageService;
        this.simulatorService = simulatorService;
    }

    @PostMapping("/manual")
    @Operation(summary = "Log a manual meter reading")
    public ResponseEntity<ApiResponse<WaterUsageLogResponse>> recordManualReading(@Valid @RequestBody MeterReadingRequest request) {
        WaterUsageLog log = waterUsageService.recordManualReading(request);

        WaterUsageLogResponse response = WaterUsageLogResponse.builder()
                .id(log.getId())
                .meterId(log.getWaterMeter().getId())
                .meterNumber(log.getWaterMeter().getMeterNumber())
                .readingDate(log.getReadingDate())
                .currentReading(log.getCurrentReading())
                .consumption(log.getConsumption())
                .readingSource(log.getReadingSource())
                .isSimulated(log.getIsSimulated())
                .build();

        // Return the DTO
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reading recorded successfully", response));
    }

    @PostMapping("/upload-csv")
    @Operation(summary = "Bulk upload meter readings via CSV", description = "CSV Format: MeterNumber, ReadingDate (YYYY-MM-DD), Consumption (kL)")
    public ResponseEntity<ApiResponse<String>> uploadCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(waterUsageService.processCsvUpload(file)));
    }

    @PostMapping("/simulate/trigger-daily")
    @Operation(summary = "Manually trigger the daily virtual meter simulation job")
    public ResponseEntity<ApiResponse<String>> triggerDailySimulation() {
        simulatorService.triggerDailySimulation();
        return ResponseEntity.ok(ApiResponse.success("Daily simulation job triggered successfully for all virtual meters", null));
    }

    @PostMapping("/simulate/meters/{meterId}/history")
    @Operation(summary = "Generate historical simulated data for demonstrations")
    public ResponseEntity<ApiResponse<String>> generateHistoricalData(
            @PathVariable Long meterId,
            @Valid @RequestBody HistoricalSimulationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                simulatorService.generateHistoricalData(meterId, request.getStartDate(), request.getEndDate())));
    }
}