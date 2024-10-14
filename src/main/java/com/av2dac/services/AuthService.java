package com.av2dac.services;

import com.av2dac.dto.RegisterDto;
import com.av2dac.entities.User;
import com.av2dac.repositories.UserRepository;
import com.av2dac.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String registerUser(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            return "E-mail já registrado.";
        }
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            return "Nome de usuário já está em uso.";
        }

        User user = new User();
        user.setUserName(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setName(registerDto.getName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        if ("ADMIN".equalsIgnoreCase(registerDto.getRole())) {
            user.setRole(User.Role.ADMIN);
        } else {
            user.setRole(User.Role.USER); // Papel padrão é USER
        }

        userRepository.save(user);

        return "Usuário registrado com sucesso!";
    }

    public String authenticateUser(User loginDetails) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDetails.getEmail(), loginDetails.getPassword())
            );

            // Retrieve the authenticated user's details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername());

            return token;

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }
    }
}
