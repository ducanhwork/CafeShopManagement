package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Authentication response DTO
 */
public class AuthResponse {
    @SerializedName("token")
    private String token;
    
    @SerializedName("user")
    private UserInfo user;
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
    
    /**
     * User information nested class
     */
    public static class UserInfo {
        @SerializedName("id")
        private Long id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("role")
        private String role;
        
        // Getters and Setters
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
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
}
