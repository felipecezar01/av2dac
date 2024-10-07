package com.av2dac.dto;

public class RegisterDto {
    private String username;
    private String email;
    private String password;
    private String name; // Novo campo

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getName() { // Getter para o nome
        return name;
    }

    public void setName(String name) { // Setter para o nome
        this.name = name;
    }
}
