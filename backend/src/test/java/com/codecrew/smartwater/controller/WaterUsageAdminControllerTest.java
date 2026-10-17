package com.codecrew.smartwater.controller;

import com.codecrew.smartwater.dto.request.MeterReadingRequest;
import com.codecrew.smartwater.model.entity.WaterMeter;
import com.codecrew.smartwater.model.entity.WaterUsageLog;
import com.codecrew.smartwater.service.VirtualMeterSimulatorService;
import com.codecrew.smartwater.service.WaterUsageService;
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
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WaterUsageAdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private WaterUsageService waterUsageService;
    @MockBean private VirtualMeterSimulatorService simulatorService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void recordManualReading_Returns201Created() throws Exception {
        MeterReadingRequest request = new MeterReadingRequest();
        request.setMeterNumber("MTR-123");
        request.setReadingDate(LocalDate.now());
        request.setConsumption(BigDecimal.valueOf(1.5));

        WaterMeter meter = WaterMeter.builder().id(1L).meterNumber("MTR-123").build();
        WaterUsageLog log = WaterUsageLog.builder().id(1L).waterMeter(meter).consumption(BigDecimal.valueOf(1.5)).build();

        when(waterUsageService.recordManualReading(any(MeterReadingRequest.class))).thenReturn(log);

        mockMvc.perform(post("/api/v1/admin/usage/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "RESIDENT")
    void recordManualReading_WithResidentRole_Returns403Forbidden() throws Exception {
        MeterReadingRequest request = new MeterReadingRequest();
        request.setMeterNumber("MTR-123");
        request.setReadingDate(LocalDate.now());
        request.setConsumption(BigDecimal.valueOf(1.5));

        mockMvc.perform(post("/api/v1/admin/usage/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}