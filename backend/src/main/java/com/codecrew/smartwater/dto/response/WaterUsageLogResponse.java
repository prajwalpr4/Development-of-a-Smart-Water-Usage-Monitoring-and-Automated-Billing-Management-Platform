package com.codecrew.smartwater.dto.response;

import com.codecrew.smartwater.model.enums.ReadingSource;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class WaterUsageLogResponse {
    private Long id;
    private Long meterId;
    private String meterNumber;
    private LocalDate readingDate;
    private BigDecimal currentReading;
    private BigDecimal consumption;
    private ReadingSource readingSource;
    private Boolean isSimulated;
}