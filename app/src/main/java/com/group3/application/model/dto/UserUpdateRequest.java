package com.group3.application.model.dto;

import java.util.UUID;

public class UserUpdateRequest {
    private String id;
    private String email;
    private String fullname;
    private String mobile;
    private String roleId;

    public UserUpdateRequest(String id, String email, String fullname, String mobile, String roleId) {
        this.id = id;
        this.email = email;
        this.fullname = fullname;
        this.mobile = mobile;
        this.roleId = roleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
