package com.codecrew.smartwater.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApartmentRequest {
    @NotBlank(message = "Apartment name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Total flats count is required")
    @Min(value = 1, message = "Total flats must be at least 1")
    private Integer totalFlats;

    private BigDecimal commonAreaSqFt;
}