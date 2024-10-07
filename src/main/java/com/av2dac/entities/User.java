package com.av2dac.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users") // Define o nome da tabela como "users" para evitar conflito com palavras reservadas
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // Adicionei o campo username
    private String name;
    private String email;
    private String password;

    // Adicione um campo para armazenar o papel (role) do usuário
    private String role; // Pode ser "USER", "ADMIN", etc.

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() { // Adicionei o método getter para username
        return username;
    }

    public void setUsername(String username) { // Adicionei o método setter para username
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Adicione o método getRole para acessar o papel do usuário
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Método que retorna as autoridades (roles) do usuário
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }
}
