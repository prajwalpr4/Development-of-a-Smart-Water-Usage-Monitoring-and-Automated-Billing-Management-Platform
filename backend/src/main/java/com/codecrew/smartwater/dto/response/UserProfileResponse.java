package com.codecrew.smartwater.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String apartmentName;
    private String flatNumber;
    private BigDecimal areaSqFt;
    private String meterNumber;
}