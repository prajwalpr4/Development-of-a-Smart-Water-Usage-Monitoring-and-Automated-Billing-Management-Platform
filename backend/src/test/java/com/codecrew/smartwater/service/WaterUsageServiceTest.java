package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.request.MeterReadingRequest;
import com.codecrew.smartwater.exception.ApiException;
import com.codecrew.smartwater.model.entity.WaterMeter;
import com.codecrew.smartwater.model.entity.WaterUsageLog;
import com.codecrew.smartwater.repository.WaterMeterRepository;
import com.codecrew.smartwater.repository.WaterUsageLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaterUsageServiceTest {

    @Mock
    private WaterUsageLogRepository logRepository;

    @Mock
    private WaterMeterRepository meterRepository;

    @InjectMocks
    private WaterUsageService waterUsageService;

    @Test
    void recordManualReading_Success() {
        MeterReadingRequest request = new MeterReadingRequest();
        request.setMeterNumber("MTR-123");
        request.setReadingDate(LocalDate.now());
        request.setConsumption(BigDecimal.valueOf(1.5));

        WaterMeter meter = WaterMeter.builder().id(1L).meterNumber("MTR-123").lastReadingValue(BigDecimal.ZERO).build();
        WaterUsageLog savedLog = WaterUsageLog.builder().id(1L).consumption(BigDecimal.valueOf(1.5)).build();

        when(meterRepository.findByMeterNumber("MTR-123")).thenReturn(Optional.of(meter));
        when(logRepository.existsByWaterMeterIdAndReadingDate(1L, request.getReadingDate())).thenReturn(false);
        when(logRepository.save(any(WaterUsageLog.class))).thenReturn(savedLog);

        WaterUsageLog result = waterUsageService.recordManualReading(request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1.5), result.getConsumption());
        verify(logRepository, times(1)).save(any(WaterUsageLog.class));
    }

    @Test
    void recordManualReading_ThrowsExceptionWhenDuplicate() {
        MeterReadingRequest request = new MeterReadingRequest();
        request.setMeterNumber("MTR-123");
        request.setReadingDate(LocalDate.now());
        request.setConsumption(BigDecimal.valueOf(1.5));

        WaterMeter meter = WaterMeter.builder().id(1L).meterNumber("MTR-123").lastReadingValue(BigDecimal.ZERO).build();

        when(meterRepository.findByMeterNumber("MTR-123")).thenReturn(Optional.of(meter));
        when(logRepository.existsByWaterMeterIdAndReadingDate(1L, request.getReadingDate())).thenReturn(true);

        assertThrows(ApiException.class, () -> waterUsageService.recordManualReading(request));
    }
}