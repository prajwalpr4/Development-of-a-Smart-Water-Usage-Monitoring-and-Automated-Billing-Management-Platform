package com.codecrew.smartwater.repository;

import com.codecrew.smartwater.model.entity.WaterUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaterUsageLogRepository extends JpaRepository<WaterUsageLog, Long> {

    Optional<WaterUsageLog> findByWaterMeterIdAndReadingDate(Long meterId, LocalDate readingDate);

    boolean existsByWaterMeterIdAndReadingDate(Long meterId, LocalDate readingDate);

    Optional<WaterUsageLog> findTopByWaterMeterIdOrderByReadingDateDesc(Long meterId);

    List<WaterUsageLog> findByWaterMeterIdAndReadingDateBetweenOrderByReadingDateAsc(
            Long meterId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT l FROM WaterUsageLog l WHERE l.waterMeter.household.apartment.id = :apartmentId " +
            "AND l.readingDate BETWEEN :startDate AND :endDate ORDER BY l.readingDate ASC")
    List<WaterUsageLog> findAllByApartmentAndDateRange(
            @Param("apartmentId") Long apartmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}