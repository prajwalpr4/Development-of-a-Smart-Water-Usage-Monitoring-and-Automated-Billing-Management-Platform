package com.codecrew.smartwater.service;

import com.codecrew.smartwater.exception.ResourceNotFoundException;
import com.codecrew.smartwater.model.entity.WaterMeter;
import com.codecrew.smartwater.model.enums.MeterStatus;
import com.codecrew.smartwater.model.enums.ReadingSource;
import com.codecrew.smartwater.repository.WaterMeterRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VirtualMeterSimulatorService {

    private final WaterMeterRepository meterRepository;
    private final WaterUsageService waterUsageService;

    @Value("${app.simulator.min-daily-liters:200.0}")
    private double minDailyLiters;

    @Value("${app.simulator.max-daily-liters:800.0}")
    private double maxDailyLiters;

    public VirtualMeterSimulatorService(WaterMeterRepository meterRepository, WaterUsageService waterUsageService) {
        this.meterRepository = meterRepository;
        this.waterUsageService = waterUsageService;
    }

//  Runs daily at 1:00 AM server time to simulate IoT gateway data drops
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void triggerDailySimulation() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<WaterMeter> virtualMeters = meterRepository.findAllByStatusAndIsVirtual(MeterStatus.ACTIVE, true);

        int count = 0;
        for (WaterMeter meter : virtualMeters) {
            try {
                BigDecimal randomConsumption = generateRandomConsumption();
                waterUsageService.processAndSaveReading(meter, yesterday, randomConsumption, ReadingSource.SIMULATED, true);
                count++;
            } catch (Exception ignored) {
                // Skips if reading already exists (handled safely by WaterUsageService)
            }
        }
        System.out.println("Automated Simulation Complete. Processed meters: " + count);
    }

    @Transactional
    public String generateHistoricalData(Long meterId, LocalDate startDate, LocalDate endDate) {
        WaterMeter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", meterId));

        if (!meter.getIsVirtual()) {
            return "Skipped. Meter is not a virtual meter.";
        }

        int generatedCount = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            try {
                BigDecimal consumption = generateRandomConsumption();
                waterUsageService.processAndSaveReading(meter, currentDate, consumption, ReadingSource.SIMULATED, true);
                generatedCount++;
            } catch (Exception ignored) {
                // Skip days that already have data to prevent duplication crashes
            }
            currentDate = currentDate.plusDays(1);
        }

        return "Successfully generated " + generatedCount + " historical readings for meter " + meter.getMeterNumber();
    }

    private BigDecimal generateRandomConsumption() {
        double randomValue = ThreadLocalRandom.current().nextDouble(minDailyLiters, maxDailyLiters);
        // Convert to Kiloliters (kL) as the base billing unit (e.g., 450 Liters = 0.450 kL)
        double kiloliters = randomValue / 1000.0;
        return BigDecimal.valueOf(kiloliters).setScale(3, RoundingMode.HALF_UP);
    }
}