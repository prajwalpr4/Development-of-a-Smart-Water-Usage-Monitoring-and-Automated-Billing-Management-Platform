package com.codecrew.smartwater.controller;

import com.codecrew.smartwater.dto.request.ApartmentRequest;
import com.codecrew.smartwater.dto.response.ApartmentResponse;
import com.codecrew.smartwater.service.ApartmentService;
import com.codecrew.smartwater.service.HouseholdService;
import com.codecrew.smartwater.service.WaterMeterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Forces Spring to load application-test.yml
class InfrastructureAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApartmentService apartmentService;

    @MockBean
    private HouseholdService householdService;

    @MockBean
    private WaterMeterService waterMeterService;

    @Test
    @WithMockUser(roles = "ADMIN") // Bypasses JWT filter for tests and simulates an Admin user
    void createApartment_Returns201Created() throws Exception {
        ApartmentRequest request = new ApartmentRequest();
        request.setName("Integration Heights");
        request.setAddress("404 Mock Street");
        request.setTotalFlats(20);
        request.setCommonAreaSqFt(BigDecimal.valueOf(500));

        ApartmentResponse mockResponse = ApartmentResponse.builder()
                .id(1L)
                .name("Integration Heights")
                .address("404 Mock Street")
                .totalFlats(20)
                .build();

        when(apartmentService.createApartment(any(ApartmentRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/admin/apartments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Apartment created successfully"))
                .andExpect(jsonPath("$.data.name").value("Integration Heights"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(roles = "RESIDENT") // Simulates a resident attempting an Admin action
    void createApartment_WithResidentRole_Returns403Forbidden() throws Exception {
        ApartmentRequest request = new ApartmentRequest();
        request.setName("Integration Heights");
        request.setAddress("404 Mock Street");
        request.setTotalFlats(20);

        mockMvc.perform(post("/api/v1/admin/apartments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Security Config intercepts and blocks
    }
}