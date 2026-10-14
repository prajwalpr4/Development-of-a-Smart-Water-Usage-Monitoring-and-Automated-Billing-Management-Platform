package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.request.ApartmentRequest;
import com.codecrew.smartwater.dto.response.ApartmentResponse;
import com.codecrew.smartwater.exception.ApiException;
import com.codecrew.smartwater.exception.ResourceNotFoundException;
import com.codecrew.smartwater.model.entity.Apartment;
import com.codecrew.smartwater.repository.ApartmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;

    public ApartmentService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    @Transactional
    public ApartmentResponse createApartment(ApartmentRequest request) {
        if (apartmentRepository.existsByName(request.getName())) {
            throw new ApiException("Apartment with this name already exists", HttpStatus.BAD_REQUEST);
        }

        Apartment apartment = Apartment.builder()
                .name(request.getName())
                .address(request.getAddress())
                .totalFlats(request.getTotalFlats())
                .commonAreaSqFt(request.getCommonAreaSqFt())
                .build();

        Apartment savedApartment = apartmentRepository.save(apartment);
        return mapToResponse(savedApartment);
    }

    public List<ApartmentResponse> getAllApartments() {
        return apartmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ApartmentResponse getApartmentById(Long id) {
        Apartment apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", id));
        return mapToResponse(apartment);
    }

    private ApartmentResponse mapToResponse(Apartment apartment) {
        return ApartmentResponse.builder()
                .id(apartment.getId())
                .name(apartment.getName())
                .address(apartment.getAddress())
                .totalFlats(apartment.getTotalFlats())
                .commonAreaSqFt(apartment.getCommonAreaSqFt())
                .build();
    }
}