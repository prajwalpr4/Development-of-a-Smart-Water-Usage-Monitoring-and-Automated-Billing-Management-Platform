package com.codecrew.smartwater.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class HouseholdResponse {
    private Long id;
    private String flatNumber;
    private Integer floor;
    private BigDecimal areaSqFt;
    private Integer occupantsCount;
    private Long apartmentId;
    private Long residentId;
    private String residentName;
}