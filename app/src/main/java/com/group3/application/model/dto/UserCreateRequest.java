package com.group3.application.model.dto;

public class UserCreateRequest {
    private String email;
    private String password;
    private String fullname;
    private String mobile;
    private String roleId;

    public UserCreateRequest(String email, String password, String fullname, String mobile, String roleId) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.mobile = mobile;
        this.roleId = roleId;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
