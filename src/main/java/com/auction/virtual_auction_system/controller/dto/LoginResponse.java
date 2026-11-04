package com.auction.virtual_auction_system.controller.dto;

public class LoginResponse {

    private Long id;

    private String name;

    private String email;

    private String token;

    private String role;

    public LoginResponse(
            Long id,
            String name,
            String email,
            String token,
            String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.token=token;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
