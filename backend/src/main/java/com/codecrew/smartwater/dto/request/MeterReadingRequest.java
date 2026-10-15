package com.codecrew.smartwater.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MeterReadingRequest {
    @NotBlank(message = "Meter number is required")
    private String meterNumber;

    @NotNull(message = "Reading date is required")
    private LocalDate readingDate;

    @NotNull(message = "Consumption is required")
    @Positive(message = "Consumption must be positive")
    private BigDecimal consumption;
}