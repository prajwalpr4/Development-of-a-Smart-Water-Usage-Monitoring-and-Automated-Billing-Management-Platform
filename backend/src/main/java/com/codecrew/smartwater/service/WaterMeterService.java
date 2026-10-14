package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.request.WaterMeterRequest;
import com.codecrew.smartwater.dto.response.WaterMeterResponse;
import com.codecrew.smartwater.exception.ApiException;
import com.codecrew.smartwater.exception.ResourceNotFoundException;
import com.codecrew.smartwater.model.entity.Household;
import com.codecrew.smartwater.model.entity.WaterMeter;
import com.codecrew.smartwater.model.enums.MeterStatus;
import com.codecrew.smartwater.repository.HouseholdRepository;
import com.codecrew.smartwater.repository.WaterMeterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WaterMeterService {

    private final WaterMeterRepository waterMeterRepository;
    private final HouseholdRepository householdRepository;

    public WaterMeterService(WaterMeterRepository waterMeterRepository, HouseholdRepository householdRepository) {
        this.waterMeterRepository = waterMeterRepository;
        this.householdRepository = householdRepository;
    }

    @Transactional
    public WaterMeterResponse createWaterMeter(WaterMeterRequest request) {
        if (waterMeterRepository.existsByMeterNumber(request.getMeterNumber())) {
            throw new ApiException("Meter number already in use", HttpStatus.BAD_REQUEST);
        }

        Household household = householdRepository.findById(request.getHouseholdId())
                .orElseThrow(() -> new ResourceNotFoundException("Household", "id", request.getHouseholdId()));

        if (household.getWaterMeter() != null) {
            throw new ApiException("Household already has an active water meter", HttpStatus.BAD_REQUEST);
        }

        WaterMeter meter = WaterMeter.builder()
                .meterNumber(request.getMeterNumber())
                .installationDate(request.getInstallationDate())
                .isVirtual(request.getIsVirtual())
                .status(MeterStatus.ACTIVE)
                .lastReadingValue(BigDecimal.ZERO)
                .household(household)
                .build();

        WaterMeter savedMeter = waterMeterRepository.save(meter);
        return mapToResponse(savedMeter);
    }

    public WaterMeterResponse getMeterById(Long id) {
        WaterMeter meter = waterMeterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", id));
        return mapToResponse(meter);
    }

    private WaterMeterResponse mapToResponse(WaterMeter meter) {
        return WaterMeterResponse.builder()
                .id(meter.getId())
                .meterNumber(meter.getMeterNumber())
                .installationDate(meter.getInstallationDate())
                .status(meter.getStatus())
                .isVirtual(meter.getIsVirtual())
                .lastReadingValue(meter.getLastReadingValue())
                .lastReadingDate(meter.getLastReadingDate())
                .householdId(meter.getHousehold().getId())
                .flatNumber(meter.getHousehold().getFlatNumber())
                .build();
    }
}