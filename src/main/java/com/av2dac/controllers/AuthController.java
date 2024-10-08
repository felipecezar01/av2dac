package com.av2dac.controllers;

import com.av2dac.dto.RegisterDto;
import com.av2dac.entities.User;
import com.av2dac.repositories.UserRepository;
import com.av2dac.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

class LoginResponse {
    private String message;
    private String token;

    public LoginResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

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
        // Verifica se o e-mail ou nome de usuário já está em uso
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-mail já registrado.");
        }
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome de usuário já está em uso.");
        }

        // Cria um novo usuário
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setName(registerDto.getName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // Define o papel padrão como USER
        user.setRole(User.Role.USER); // Aqui estamos definindo o papel padrão

        // Salva o usuário no banco de dados
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
    }

    // Endpoint para login de usuário
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginDetails) {
        Optional<User> userOptional = userRepository.findByEmail(loginDetails.getEmail());

        if (userOptional.isPresent() && passwordEncoder.matches(loginDetails.getPassword(), userOptional.get().getPassword())) {
            String token = jwtUtil.generateToken(userOptional.get().getEmail());
            return ResponseEntity.ok(new LoginResponse("Login bem-sucedido!", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
    }

    // Endpoint para obter informações do usuário autenticado
    @GetMapping("/userinfo")
    public ResponseEntity<User> getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        return userOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
