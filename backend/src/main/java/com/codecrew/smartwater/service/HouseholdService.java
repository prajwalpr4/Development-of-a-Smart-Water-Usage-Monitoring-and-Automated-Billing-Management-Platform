package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.request.HouseholdRequest;
import com.codecrew.smartwater.dto.response.HouseholdResponse;
import com.codecrew.smartwater.exception.ApiException;
import com.codecrew.smartwater.exception.ResourceNotFoundException;
import com.codecrew.smartwater.model.entity.Apartment;
import com.codecrew.smartwater.model.entity.Household;
import com.codecrew.smartwater.model.entity.User;
import com.codecrew.smartwater.repository.ApartmentRepository;
import com.codecrew.smartwater.repository.HouseholdRepository;
import com.codecrew.smartwater.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;

    public HouseholdService(HouseholdRepository householdRepository, ApartmentRepository apartmentRepository, UserRepository userRepository) {
        this.householdRepository = householdRepository;
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public HouseholdResponse createHousehold(HouseholdRequest request) {
        if (householdRepository.existsByApartmentIdAndFlatNumber(request.getApartmentId(), request.getFlatNumber())) {
            throw new ApiException("Flat number already exists in this apartment", HttpStatus.BAD_REQUEST);
        }

        Apartment apartment = apartmentRepository.findById(request.getApartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", request.getApartmentId()));

        Household household = Household.builder()
                .flatNumber(request.getFlatNumber())
                .floor(request.getFloor())
                .areaSqFt(request.getAreaSqFt())
                .occupantsCount(request.getOccupantsCount())
                .apartment(apartment)
                .build();

        Household savedHousehold = householdRepository.save(household);
        return mapToResponse(savedHousehold);
    }

    @Transactional
    public HouseholdResponse assignResident(Long householdId, Long residentId) {
        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new ResourceNotFoundException("Household", "id", householdId));

        if (household.getResident() != null) {
            throw new ApiException("Household already has a resident assigned", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(residentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", residentId));

        household.setResident(user);
        Household updatedHousehold = householdRepository.save(household);

        return mapToResponse(updatedHousehold);
    }

    public List<HouseholdResponse> getHouseholdsByApartment(Long apartmentId) {
        return householdRepository.findByApartmentId(apartmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private HouseholdResponse mapToResponse(Household household) {
        return HouseholdResponse.builder()
                .id(household.getId())
                .flatNumber(household.getFlatNumber())
                .floor(household.getFloor())
                .areaSqFt(household.getAreaSqFt())
                .occupantsCount(household.getOccupantsCount())
                .apartmentId(household.getApartment().getId())
                .residentId(household.getResident() != null ? household.getResident().getId() : null)
                .residentName(household.getResident() != null ?
                        household.getResident().getFirstName() + " " + household.getResident().getLastName() : null)
                .build();
    }
}