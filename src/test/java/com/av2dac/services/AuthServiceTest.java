package com.av2dac.services;

import com.av2dac.dto.RegisterDto;
import com.av2dac.entities.User;
import com.av2dac.repositories.UserRepository;
import com.av2dac.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("testuser");
        registerDto.setEmail("test@example.com");
        registerDto.setName("Test User");
        registerDto.setPassword("password");
        registerDto.setRole("USER");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        String result = authService.registerUser(registerDto);

        // Assert
        assertEquals("Usuário registrado com sucesso!", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("existing@example.com");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        // Act
        String result = authService.registerUser(registerDto);

        // Assert
        assertEquals("E-mail já registrado.", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_UsernameAlreadyExists() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("existinguser");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        // Act
        String result = authService.registerUser(registerDto);

        // Assert
        assertEquals("Nome de usuário já está em uso.", result);
        verify(userRepository, never()).save(any(User.class));
    }
}
