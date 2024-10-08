package com.av2dac.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}") // Carrega a chave secreta do arquivo de configuração
    private String SECRET_KEY; // Use uma chave segura em produção

    // Método para gerar o token JWT
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expira em 10 horas
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Método para extrair o email (username) do token JWT
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Método para verificar se o token expirou
    public Boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Método para validar o token JWT
    public Boolean validateToken(String token, String email) {
        return (extractUsername(token).equals(email) && !isTokenExpired(token));
    }

    // Método para extrair todas as Claims do token
    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Token inválido ou expirado."); // Lança exceção caso o token não seja válido
        }
    }
}
