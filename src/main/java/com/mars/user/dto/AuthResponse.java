package com.mars.user.dto;

import com.mars.common.entity.User;

public class AuthResponse {
    private String token;
    private User user;
    private String redirectUrl;

    
    public AuthResponse(String token, User user, String redirectUrl) {
        this.token = token;
        this.user = user;
        this.redirectUrl = redirectUrl;
    }

    
    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
        
        this.redirectUrl = user.getRole().equals("customer") ? "/customer-dashboard" : "/clerkhome";
    }

    
    public AuthResponse() {
    }

    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        
        if (user != null) {
            this.redirectUrl = user.getRole().equals("customer") ? "/customer-dashboard" : "/clerkhome";
        }
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", user=" + user +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}