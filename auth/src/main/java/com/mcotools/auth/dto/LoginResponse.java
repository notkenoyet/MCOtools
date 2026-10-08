package com.mcotools.auth.dto;

public class LoginResponse {

    private String token;
    private String nom;
    private String email;

    public LoginResponse(String token, String nom, String email) {
        this.token = token;
        this.nom = nom;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }
}
