package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.request.WaterMeterRequest;
import com.codecrew.smartwater.dto.response.WaterMeterResponse;
import com.codecrew.smartwater.exception.ApiException;
import com.codecrew.smartwater.model.entity.Household;
import com.codecrew.smartwater.model.entity.WaterMeter;
import com.codecrew.smartwater.repository.HouseholdRepository;
import com.codecrew.smartwater.repository.WaterMeterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaterMeterServiceTest {

    @Mock private WaterMeterRepository waterMeterRepository;
    @Mock private HouseholdRepository householdRepository;

    @InjectMocks private WaterMeterService waterMeterService;

    @Test
    void createWaterMeter_Success() {
        WaterMeterRequest request = new WaterMeterRequest();
        request.setMeterNumber("MTR-100");
        request.setHouseholdId(1L);
        request.setInstallationDate(LocalDate.now());
        request.setIsVirtual(true);

        Household household = Household.builder().id(1L).flatNumber("A-1").build();
        WaterMeter meter = WaterMeter.builder().id(1L).meterNumber("MTR-100").household(household).build();

        when(waterMeterRepository.existsByMeterNumber("MTR-100")).thenReturn(false);
        when(householdRepository.findById(1L)).thenReturn(Optional.of(household));
        when(waterMeterRepository.save(any(WaterMeter.class))).thenReturn(meter);

        WaterMeterResponse response = waterMeterService.createWaterMeter(request);

        assertNotNull(response);
        assertEquals("MTR-100", response.getMeterNumber());
    }

    @Test
    void createWaterMeter_ThrowsException_WhenMeterExists() {
        WaterMeterRequest request = new WaterMeterRequest();
        request.setMeterNumber("MTR-100");

        when(waterMeterRepository.existsByMeterNumber("MTR-100")).thenReturn(true);

        assertThrows(ApiException.class, () -> waterMeterService.createWaterMeter(request));
    }
}