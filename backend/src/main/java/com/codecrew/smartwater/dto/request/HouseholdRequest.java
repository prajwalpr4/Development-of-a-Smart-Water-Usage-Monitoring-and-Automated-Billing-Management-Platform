package com.codecrew.smartwater.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HouseholdRequest {
    @NotBlank(message = "Flat number is required")
    private String flatNumber;

    @NotNull(message = "Floor is required")
    private Integer floor;

    @NotNull(message = "Area is required")
    @Positive(message = "Area must be positive")
    private BigDecimal areaSqFt;

    @NotNull(message = "Occupants count is required")
    @Min(value = 1, message = "At least 1 occupant is required")
    private Integer occupantsCount;

    @NotNull(message = "Apartment ID is required")
    private Long apartmentId;
}