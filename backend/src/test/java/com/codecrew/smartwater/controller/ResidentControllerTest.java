package com.codecrew.smartwater.controller;

import com.codecrew.smartwater.dto.response.UserProfileResponse;
import com.codecrew.smartwater.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ResidentControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;

    @Test
    @WithMockUser(username = "resident@local.com", roles = "RESIDENT")
    void getProfile_Returns200OK() throws Exception {
        UserProfileResponse profile = UserProfileResponse.builder().email("resident@local.com").build();
        when(userService.getUserProfile(anyString())).thenReturn(profile);

        mockMvc.perform(get("/api/v1/resident/profile"))
                .andExpect(status().isOk());
    }

    @Test
    void getProfile_WithoutAuth_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/resident/profile"))
                .andExpect(status().isForbidden());
    }
}