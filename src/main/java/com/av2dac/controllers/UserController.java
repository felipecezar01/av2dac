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

    private final UserService userService; // Serviço para gerenciamento de usuários
    private final PasswordEncoder passwordEncoder; // Encoder para criptografar senhas
    private final JwtUtil jwtUtil; // Utilitário para manipulação de JWT

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Endpoint para atualizar informações do usuário - Acesso para USER e ADMIN
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody User userDetails, Authentication authentication) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            String loggedInUserEmail = authentication.getName();

            // Verifica se o usuário é admin ou o próprio dono da conta
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = userToUpdate.getEmail().equals(loggedInUserEmail);

            if (isAdmin || isOwner) {
                // Atualiza os campos informados pelo usuário
                if (userDetails.getName() != null) userToUpdate.setName(userDetails.getName());
                if (userDetails.getUserName() != null) userToUpdate.setUserName(userDetails.getUserName());
                if (userDetails.getEmail() != null) userToUpdate.setEmail(userDetails.getEmail());
                if (userDetails.getPassword() != null) userToUpdate.setPassword(passwordEncoder.encode(userDetails.getPassword()));

                userService.saveUser(userToUpdate); // Salva as mudanças no banco

                String newToken = jwtUtil.generateToken(userToUpdate.getEmail()); // Gera um novo token JWT

                Map<String, String> response = new HashMap<>();
                response.put("token", newToken); // Retorna o novo token JWT
                response.put("message", "Usuário atualizado com sucesso");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Retorna erro se o usuário não tiver permissão
            }
        } else {
            return ResponseEntity.notFound().build(); // Retorna erro 404 se o usuário não for encontrado
        }
    }

    // Endpoint para deletar um usuário - Apenas ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id); // Busca o usuário pelo ID
        if (userOptional.isPresent()) {
            userService.deleteUser(userOptional.get()); // Deleta o usuário
            return ResponseEntity.noContent().build(); // Retorna status de sucesso sem conteúdo
        } else {
            return ResponseEntity.notFound().build(); // Retorna erro 404 se o usuário não for encontrado
        }
    }

    // Endpoint para o usuário gerar QR code de suas informações
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/qrcode")
    public ResponseEntity<byte[]> getUserQrCode(Authentication authentication) throws WriterException, IOException {
        String email = authentication.getName(); // Obtém o e-mail do usuário autenticado
        Optional<User> userOptional = userService.findByEmail(email); // Busca o usuário pelo e-mail
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Gera o conteúdo do QR code com as informações do usuário
            String qrContent = generateUserQrContent(user);

            // Gera a imagem do QR code
            byte[] qrImage = generateQrCodeImage(qrContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "user_" + user.getId() + "_qrcode.png");

            return new ResponseEntity<>(qrImage, headers, HttpStatus.OK); // Retorna a imagem do QR code
        } else {
            return ResponseEntity.notFound().build(); // Retorna erro 404 se o usuário não for encontrado
        }
    }

    // Método para gerar o conteúdo do QR code
    private String generateUserQrContent(User user) {
        return "Usuário ID: " + user.getId() +
                "\nNome: " + user.getName() +
                "\nUsername: " + user.getUserName() +
                "\nEmail: " + user.getEmail(); // Formata as informações do usuário para o QR code
    }

    // Método para gerar a imagem do QR code com suporte a UTF-8
    private byte[] generateQrCodeImage(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300; // Define a largura da imagem
        int height = 300; // Define a altura da imagem

        // Configurações do QR code com suporte a UTF-8
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream); // Escreve o QR code em um fluxo de bytes
        return pngOutputStream.toByteArray(); // Retorna a imagem do QR code como array de bytes
    }
}
