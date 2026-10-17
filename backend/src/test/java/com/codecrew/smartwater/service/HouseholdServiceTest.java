package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.request.HouseholdRequest;
import com.codecrew.smartwater.dto.response.HouseholdResponse;
import com.codecrew.smartwater.exception.ApiException;
import com.codecrew.smartwater.model.entity.Apartment;
import com.codecrew.smartwater.model.entity.Household;
import com.codecrew.smartwater.repository.ApartmentRepository;
import com.codecrew.smartwater.repository.HouseholdRepository;
import com.codecrew.smartwater.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseholdServiceTest {

    @Mock private HouseholdRepository householdRepository;
    @Mock private ApartmentRepository apartmentRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private HouseholdService householdService;

    @Test
    void createHousehold_Success() {
        HouseholdRequest request = new HouseholdRequest();
        request.setFlatNumber("A-101");
        request.setApartmentId(1L);
        request.setFloor(1);
        request.setAreaSqFt(BigDecimal.valueOf(1000));
        request.setOccupantsCount(3);

        Apartment apt = Apartment.builder().id(1L).name("Test Apt").build();
        Household savedHousehold = Household.builder().id(1L).flatNumber("A-101").apartment(apt).build();

        when(householdRepository.existsByApartmentIdAndFlatNumber(1L, "A-101")).thenReturn(false);
        when(apartmentRepository.findById(1L)).thenReturn(Optional.of(apt));
        when(householdRepository.save(any(Household.class))).thenReturn(savedHousehold);

        HouseholdResponse response = householdService.createHousehold(request);

        assertNotNull(response);
        assertEquals("A-101", response.getFlatNumber());
        assertEquals(1L, response.getApartmentId());
    }

    @Test
    void createHousehold_ThrowsException_WhenFlatExists() {
        HouseholdRequest request = new HouseholdRequest();
        request.setFlatNumber("A-101");
        request.setApartmentId(1L);

        when(householdRepository.existsByApartmentIdAndFlatNumber(1L, "A-101")).thenReturn(true);

        assertThrows(ApiException.class, () -> householdService.createHousehold(request));
    }
}