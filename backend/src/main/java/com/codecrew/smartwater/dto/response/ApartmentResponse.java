package com.codecrew.smartwater.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ApartmentResponse {
    private Long id;
    private String name;
    private String address;
    private Integer totalFlats;
    private BigDecimal commonAreaSqFt;
}