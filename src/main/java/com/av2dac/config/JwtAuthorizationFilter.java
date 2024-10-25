package com.av2dac.config;

// Importa classes utilitárias para validação e extração de dados do JWT
import com.av2dac.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
// Importa o serviço de autenticação do usuário
import com.av2dac.services.UserDetailsServiceImpl;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; // Utilitário para manipulação e validação do JWT

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Serviço que carrega os detalhes do usuário

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // Extrai o cabeçalho de autorização da requisição

        String email = null;
        String jwt = null;

        // Verifica se o cabeçalho contém um token JWT válido
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Extrai o token JWT
            email = jwtUtil.extractUsername(jwt); // Obtém o email do usuário a partir do JWT
        }

        // Autentica o usuário se o email estiver presente e não houver uma autenticação ativa
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email); // Carrega os detalhes do usuário

            // Valida o JWT e autentica o usuário
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()); // Cria o token de autenticação

                // Adiciona detalhes da requisição à autenticação
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Define a autenticação no contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // Continua o processamento do filtro
        filterChain.doFilter(request, response);
    }
}
