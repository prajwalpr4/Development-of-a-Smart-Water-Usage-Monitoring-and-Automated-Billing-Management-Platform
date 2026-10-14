package com.codecrew.smartwater.dto.response;

import com.codecrew.smartwater.model.enums.MeterStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class WaterMeterResponse {
    private Long id;
    private String meterNumber;
    private LocalDate installationDate;
    private MeterStatus status;
    private Boolean isVirtual;
    private BigDecimal lastReadingValue;
    private LocalDate lastReadingDate;
    private Long householdId;
    private String flatNumber;
}