package com.mastermind.api.service;

import com.mastermind.api.dto.LoginDTO;
import com.mastermind.api.dto.RegisterDTO;
import com.mastermind.api.model.User;
import com.mastermind.api.repository.UserRepository;
import com.mastermind.api.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@mail.com")
                .password("hashed_password")
                .build();
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        RegisterDTO dto = new RegisterDTO("testuser", "test@mail.com", "123456");
        
        when(userRepository.findByEmailOrUsername(dto.email(), dto.username())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.password())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User savedUser = authService.register(dto);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingUser() {
        RegisterDTO dto = new RegisterDTO("testuser", "test@mail.com", "123456");
        
        when(userRepository.findByEmailOrUsername(dto.email(), dto.username())).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalArgumentException.class, () -> authService.register(dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginDTO dto = new LoginDTO("test@mail.com", "123456");

        when(userRepository.findByEmailOrUsername(dto.login(), dto.login())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(dto.password(), mockUser.getPassword())).thenReturn(true);
        when(tokenService.generateToken(mockUser)).thenReturn("mocked-jwt-token");

        String token = authService.login(dto);

        assertEquals("mocked-jwt-token", token);
    }
}