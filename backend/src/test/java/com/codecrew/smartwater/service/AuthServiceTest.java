package com.codecrew.smartwater.service;

import com.codecrew.smartwater.dto.request.LoginRequest;
import com.codecrew.smartwater.dto.request.RegisterRequest;
import com.codecrew.smartwater.dto.response.AuthResponse;
import com.codecrew.smartwater.exception.ApiException;
import com.codecrew.smartwater.model.entity.User;
import com.codecrew.smartwater.model.enums.Role;
import com.codecrew.smartwater.repository.UserRepository;
import com.codecrew.smartwater.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("test@local.com")
                .firstName("Test")
                .lastName("User")
                .role(Role.ROLE_RESIDENT)
                .build();
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@local.com");
        request.setPassword("pass123");
        request.setFirstName("Test");
        request.setLastName("User");

        Authentication mockAuth = mock(Authentication.class);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        lenient().when(mockAuth.getPrincipal()).thenReturn(mockUser);
        lenient().when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("mockJwtToken");
        lenient().when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("mockJwtToken");

        AuthResponse response = authService.registerUser(request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@local.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(ApiException.class, () -> authService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@local.com");
        request.setPassword("pass123");

        Authentication mockAuth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockUser); // Prevent NullPointerException
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("mockJwtToken");

        AuthResponse response = authService.authenticateUser(request);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
    }
}