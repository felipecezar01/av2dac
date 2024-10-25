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
    private AuthService authService; // Serviço para autenticação e registro

    @Autowired
    private EmailService emailService; // Serviço para envio de e-mails

    @Autowired
    private UserService userService; // Serviço para gerenciamento de usuários

    // Endpoint para registro de usuário
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDto registerDto) {
        String response = authService.registerUser(registerDto); // Registra o usuário

        if ("Usuário registrado com sucesso!".equals(response)) {
            Optional<User> userOptional = userService.findByEmail(registerDto.getEmail()); // Busca o usuário pelo e-mail
            userOptional.ifPresent(emailService::sendRegistrationEmail); // Envia e-mail de confirmação
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // Retorna resposta de sucesso
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // Retorna erro caso o registro falhe
        }
    }

    // Endpoint para login de usuário
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginDetails) {
        try {
            String token = authService.authenticateUser(loginDetails); // Autentica o usuário e gera o token JWT
            return ResponseEntity.ok(new LoginResponse("Login bem-sucedido!", token)); // Retorna resposta de sucesso com o token
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas."); // Retorna erro se as credenciais forem inválidas
        }
    }

    // Endpoint para obter informações do usuário autenticado
    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado."); // Retorna erro se o usuário não estiver autenticado
        }

        String email = authentication.getName(); // Obtém o e-mail do usuário autenticado
        Optional<User> userOptional = userService.findByEmail(email); // Busca o usuário pelo e-mail

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok(user); // Retorna informações do usuário autenticado
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado."); // Retorna erro se o usuário não for encontrado
        }
    }
}
