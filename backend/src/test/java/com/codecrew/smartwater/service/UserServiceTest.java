package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.response.UserProfileResponse;
import com.codecrew.smartwater.model.entity.User;
import com.codecrew.smartwater.model.enums.Role;
import com.codecrew.smartwater.repository.HouseholdRepository;
import com.codecrew.smartwater.repository.UserRepository;
import com.codecrew.smartwater.repository.WaterUsageLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private HouseholdRepository householdRepository;
    @Mock private WaterUsageLogRepository logRepository;

    @InjectMocks private UserService userService;

    @Test
    void getUserProfile_Success_WithoutHousehold() {
        User user = User.builder().id(1L).email("test@local.com").firstName("Test").lastName("User").role(Role.ROLE_RESIDENT).build();

        when(userRepository.findByEmail("test@local.com")).thenReturn(Optional.of(user));
        when(householdRepository.findByResidentId(1L)).thenReturn(Optional.empty());

        UserProfileResponse response = userService.getUserProfile("test@local.com");

        assertNotNull(response);
        assertEquals("test@local.com", response.getEmail());
        assertNull(response.getFlatNumber()); // Because no household assigned
    }
}