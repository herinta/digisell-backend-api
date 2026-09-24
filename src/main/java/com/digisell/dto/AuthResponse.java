package com.digisell.dto;

import com.digisell.model.Seller;

public class AuthResponse {

    private String token;
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String bio;
    private String avatarUrl;

    public AuthResponse() {}

    public AuthResponse(String token, Seller seller) {
        this.token = token;
        this.id = seller.getId();
        this.username = seller.getUsername();
        this.email = seller.getEmail();
        this.fullName = seller.getFullName();
        this.bio = seller.getBio();
        this.avatarUrl = seller.getAvatarUrl();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
