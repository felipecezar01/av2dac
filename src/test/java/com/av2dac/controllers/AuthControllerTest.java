package com.av2dac.controllers;

import com.av2dac.dto.RegisterDto;
import com.av2dac.entities.User;
import com.av2dac.services.AuthService;
import com.av2dac.services.UserService;
import com.av2dac.services.EmailService;
import com.av2dac.services.UserDetailsServiceImpl;
import com.av2dac.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Configuration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())  // Atualiza o método para o formato recomendado
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }


    @Test
    void registerUser_Success() throws Exception {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("testuser");
        registerDto.setEmail("test@example.com");
        registerDto.setName("Test User");
        registerDto.setPassword("password");
        registerDto.setRole("USER");

        when(authService.registerUser(any(RegisterDto.class))).thenReturn("Usuário registrado com sucesso!");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuário registrado com sucesso!"));
    }

    @Test
    void loginUser_Success() throws Exception {
        // Arrange
        User loginDetails = new User();
        loginDetails.setEmail("test@example.com");
        loginDetails.setPassword("password");

        when(authService.authenticateUser(any(User.class))).thenReturn("mockToken");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login bem-sucedido!"))
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getUserInfo_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole(User.Role.USER);  // Certifique-se de que o campo 'role' está preenchido

        // Mock do userService para retornar o usuário correto
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(get("/auth/userinfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("USER"));  // Verificando o campo 'role'
    }



}
