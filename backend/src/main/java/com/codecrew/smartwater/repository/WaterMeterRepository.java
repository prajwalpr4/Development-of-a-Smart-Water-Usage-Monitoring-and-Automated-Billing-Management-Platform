package com.codecrew.smartwater.repository;

import com.codecrew.smartwater.model.entity.WaterMeter;
import com.codecrew.smartwater.model.enums.MeterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaterMeterRepository extends JpaRepository<WaterMeter, Long> {
    Optional<WaterMeter> findByMeterNumber(String meterNumber);
    Optional<WaterMeter> findByHouseholdId(Long householdId);
    List<WaterMeter> findAllByStatus(MeterStatus status);
    List<WaterMeter> findAllByStatusAndIsVirtual(MeterStatus status, Boolean isVirtual);
    boolean existsByMeterNumber(String meterNumber);
}