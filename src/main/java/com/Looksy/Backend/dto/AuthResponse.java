package com.Looksy.Backend.dto;

public class AuthResponse<T> extends ApiResponse<T> {
    private String token;

    public AuthResponse(boolean success, String message, T data, String token) {
        super(success, message, data); // Call parent constructor
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}