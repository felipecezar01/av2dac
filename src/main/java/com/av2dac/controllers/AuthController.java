package com.av2dac.controllers;

import com.av2dac.dto.LoginResponse;
import com.av2dac.dto.RegisterDto;
import com.av2dac.entities.User;
import com.av2dac.services.AuthService;
import com.av2dac.services.EmailService;
import com.av2dac.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    // Endpoint para registro de usuário
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDto registerDto) {
        String response = authService.registerUser(registerDto);

        if ("Usuário registrado com sucesso!".equals(response)) {
            Optional<User> userOptional = userService.findByEmail(registerDto.getEmail());
            userOptional.ifPresent(emailService::sendRegistrationEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Endpoint para login de usuário
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginDetails) {
        try {
            String token = authService.authenticateUser(loginDetails);
            return ResponseEntity.ok(new LoginResponse("Login bem-sucedido!", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
    }

    // Endpoint para obter informações do usuário autenticado
    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        String email = authentication.getName();
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }
    }
}
