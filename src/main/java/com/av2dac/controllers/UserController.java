package com.av2dac.controllers;

import com.av2dac.entities.User;
import com.av2dac.services.UserService;
import com.av2dac.util.JwtUtil;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody User userDetails, Authentication authentication) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            String loggedInUserEmail = authentication.getName();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = userToUpdate.getEmail().equals(loggedInUserEmail);

            if (isAdmin || isOwner) {
                if (userDetails.getName() != null) userToUpdate.setName(userDetails.getName());
                if (userDetails.getUserName() != null) userToUpdate.setUserName(userDetails.getUserName());
                if (userDetails.getEmail() != null) userToUpdate.setEmail(userDetails.getEmail());
                if (userDetails.getPassword() != null) userToUpdate.setPassword(passwordEncoder.encode(userDetails.getPassword()));

                userService.saveUser(userToUpdate);

                String newToken = jwtUtil.generateToken(userToUpdate.getEmail());

                Map<String, String> response = new HashMap<>();
                response.put("token", newToken);
                response.put("message", "Usuário atualizado com sucesso");
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
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            userService.deleteUser(userOptional.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para o usuário gerar QR code de suas informações
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/qrcode")
    public ResponseEntity<byte[]> getUserQrCode(Authentication authentication) throws WriterException, IOException {
        String email = authentication.getName();
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Gerar o conteúdo do QR code
            String qrContent = generateUserQrContent(user);

            // Gerar a imagem do QR code
            byte[] qrImage = generateQrCodeImage(qrContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "user_" + user.getId() + "_qrcode.png");

            return new ResponseEntity<>(qrImage, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Método para gerar o conteúdo do QR code
    private String generateUserQrContent(User user) {
        return "Usuário ID: " + user.getId() +
                "\nNome: " + user.getName() +
                "\nUsername: " + user.getUserName() +
                "\nEmail: " + user.getEmail();
    }

    // Método para gerar a imagem do QR code com suporte a UTF-8
    private byte[] generateQrCodeImage(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300; // largura da imagem
        int height = 300; // altura da imagem

        // Configurações do QR code com UTF-8
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}
