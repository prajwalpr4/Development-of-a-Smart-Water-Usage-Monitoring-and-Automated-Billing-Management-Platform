package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.response.UserProfileResponse;
import com.codecrew.smartwater.dto.response.WaterUsageLogResponse;
import com.codecrew.smartwater.exception.ResourceNotFoundException;
import com.codecrew.smartwater.model.entity.Household;
import com.codecrew.smartwater.model.entity.User;
import com.codecrew.smartwater.model.entity.WaterMeter;
import com.codecrew.smartwater.model.entity.WaterUsageLog;
import com.codecrew.smartwater.repository.HouseholdRepository;
import com.codecrew.smartwater.repository.UserRepository;
import com.codecrew.smartwater.repository.WaterUsageLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final HouseholdRepository householdRepository;
    private final WaterUsageLogRepository logRepository;

    public UserService(UserRepository userRepository,
                       HouseholdRepository householdRepository,
                       WaterUsageLogRepository logRepository) {
        this.userRepository = userRepository;
        this.householdRepository = householdRepository;
        this.logRepository = logRepository;
    }

    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Household household = householdRepository.findByResidentId(user.getId()).orElse(null);
        WaterMeter meter = (household != null) ? household.getWaterMeter() : null;

        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .apartmentName(household != null ? household.getApartment().getName() : null)
                .flatNumber(household != null ? household.getFlatNumber() : null)
                .areaSqFt(household != null ? household.getAreaSqFt() : null)
                .meterNumber(meter != null ? meter.getMeterNumber() : null)
                .build();
    }

    public List<WaterUsageLogResponse> getResidentUsageHistory(String email, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Household household = householdRepository.findByResidentId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No household assigned to resident: " + email));

        WaterMeter meter = household.getWaterMeter();
        if (meter == null) {
            throw new ResourceNotFoundException("No water meter configured for your flat");
        }

        List<WaterUsageLog> logs = logRepository.findByWaterMeterIdAndReadingDateBetweenOrderByReadingDateAsc(
                meter.getId(), startDate, endDate);

        return logs.stream().map(log -> WaterUsageLogResponse.builder()
                .id(log.getId())
                .meterId(meter.getId())
                .meterNumber(meter.getMeterNumber())
                .readingDate(log.getReadingDate())
                .currentReading(log.getCurrentReading())
                .consumption(log.getConsumption())
                .readingSource(log.getReadingSource())
                .isSimulated(log.getIsSimulated())
                .build()
        ).collect(Collectors.toList());
    }
}