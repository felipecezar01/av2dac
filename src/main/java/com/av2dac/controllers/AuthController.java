package com.av2dac.controllers;

import com.av2dac.dto.RegisterDto; // Importe o novo DTO
import com.av2dac.entities.User;
import com.av2dac.repositories.UserRepository;
import com.av2dac.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

class LoginResponse {
    private String message;
    private String token;

    public LoginResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    // Getters e Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint para registro de usuário
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDto registerDto) {
        // Cria um novo usuário
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setName(registerDto.getName()); // Define o nome
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole("USER"); // Pode ser "USER", "ADMIN", etc.

        // Salva o usuário no banco de dados
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
    }

    // Endpoint para login de usuário
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginDetails) {
        User user = userRepository.findByEmail(loginDetails.getEmail());

        if (user != null && passwordEncoder.matches(loginDetails.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(new LoginResponse("Login bem-sucedido!", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }

    // Endpoint para obter informações do usuário autenticado
    @GetMapping("/userinfo")
    public ResponseEntity<User> getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(user);
    }
}
