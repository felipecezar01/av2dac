package com.av2dac.controllers;

import com.av2dac.entities.User;
import com.av2dac.repositories.UserRepository;
import com.av2dac.services.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "API is working!";
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody User userDetails, Authentication authentication) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User userToUpdate = user.get();
            String loggedInUserEmail = authentication.getName();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = userToUpdate.getEmail().equals(loggedInUserEmail);

            if (isAdmin || isOwner) {
                if (userDetails.getName() != null) userToUpdate.setName(userDetails.getName());
                if (userDetails.getEmail() != null) userToUpdate.setEmail(userDetails.getEmail());
                if (userDetails.getPassword() != null) userToUpdate.setPassword(passwordEncoder.encode(userDetails.getPassword()));

                userRepository.save(userToUpdate);

                String newToken = jwtTokenProvider.createToken(
                        userToUpdate.getEmail(),
                        userToUpdate.getAuthorities().stream()
                                .map(grantedAuthority -> grantedAuthority.getAuthority())
                                .collect(Collectors.toList())
                );

                Map<String, String> response = new HashMap<>();
                response.put("token", newToken);
                response.put("message", "User updated successfully");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
