package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.request.ApartmentRequest;
import com.codecrew.smartwater.dto.response.ApartmentResponse;
import com.codecrew.smartwater.exception.ApiException;
import com.codecrew.smartwater.model.entity.Apartment;
import com.codecrew.smartwater.repository.ApartmentRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ApartmentServiceTest {

    @Mock
    private ApartmentRepository apartmentRepository;

    @InjectMocks
    private ApartmentService apartmentService;

    private ApartmentRequest request;
    private Apartment apartment;

    @BeforeEach
    void setUp() {
        request = new ApartmentRequest();
        request.setName("Sunset Villas");
        request.setAddress("123 Test St");
        request.setTotalFlats(10);
        request.setCommonAreaSqFt(BigDecimal.valueOf(1000));

        apartment = Apartment.builder()
                .id(1L)
                .name("Sunset Villas")
                .address("123 Test St")
                .totalFlats(10)
                .commonAreaSqFt(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    void createApartment_Success() {
        when(apartmentRepository.existsByName(request.getName())).thenReturn(false);
        when(apartmentRepository.save(any(Apartment.class))).thenReturn(apartment);

        ApartmentResponse response = apartmentService.createApartment(request);

        assertNotNull(response);
        assertEquals("Sunset Villas", response.getName());
        assertEquals(1L, response.getId());
        verify(apartmentRepository, times(1)).save(any(Apartment.class));
    }

    @Test
    void createApartment_ThrowsExceptionWhenNameExists() {
        when(apartmentRepository.existsByName(request.getName())).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class, () -> {
            apartmentService.createApartment(request);
        });

        assertEquals("Apartment with this name already exists", exception.getMessage());
        verify(apartmentRepository, never()).save(any(Apartment.class));
    }
}