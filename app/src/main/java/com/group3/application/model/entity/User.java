package com.group3.application.model.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private UUID id;
    private String email;
    private String password;
    private String fullname;
    private String mobile;
    private String role;

    public User(UUID id, String email, String password, String fullname, String mobile, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.mobile = mobile;
        this.role = role;
    }

    public User(String email, String password, String fullname, String mobile, String role) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.mobile = mobile;
        this.role = role;
    }

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
