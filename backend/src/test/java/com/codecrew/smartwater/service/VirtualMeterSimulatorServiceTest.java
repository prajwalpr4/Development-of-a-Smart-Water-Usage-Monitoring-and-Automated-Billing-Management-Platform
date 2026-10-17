package com.codecrew.smartwater.service;

import com.codecrew.smartwater.model.entity.WaterMeter;
import com.codecrew.smartwater.model.enums.MeterStatus;
import com.codecrew.smartwater.repository.WaterMeterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VirtualMeterSimulatorServiceTest {

    @Mock private WaterMeterRepository meterRepository;
    @Mock private WaterUsageService waterUsageService;

    @InjectMocks private VirtualMeterSimulatorService simulatorService;

    @BeforeEach
    void setUp() {
        // We must manually inject the @Value properties for a raw Mockito test
        ReflectionTestUtils.setField(simulatorService, "minDailyLiters", 150.0);
        ReflectionTestUtils.setField(simulatorService, "maxDailyLiters", 950.0);
    }

    @Test
    void triggerDailySimulation_ProcessesVirtualMeters() {
        WaterMeter meter1 = WaterMeter.builder().id(1L).isVirtual(true).status(MeterStatus.ACTIVE).build();
        when(meterRepository.findAllByStatusAndIsVirtual(MeterStatus.ACTIVE, true)).thenReturn(List.of(meter1));

        simulatorService.triggerDailySimulation();

        verify(meterRepository, times(1)).findAllByStatusAndIsVirtual(MeterStatus.ACTIVE, true);

        // Verify that the save reading method was actually called
        verify(waterUsageService, times(1)).processAndSaveReading(eq(meter1), any(), any(), any(), eq(true));
    }
}