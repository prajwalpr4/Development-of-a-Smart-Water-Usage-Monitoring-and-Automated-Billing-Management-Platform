package com.codecrew.smartwater.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WaterMeterRequest {
    @NotBlank(message = "Meter number is required")
    private String meterNumber;

    @NotNull(message = "Installation date is required")
    private LocalDate installationDate;

    @NotNull(message = "isVirtual flag is required")
    private Boolean isVirtual;

    @NotNull(message = "Household ID is required")
    private Long householdId;
}