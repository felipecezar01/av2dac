package com.av2dac.dto;

public class LoginResponse {
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

    public String getToken() {
        return token;
    }
}
