package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;

public class NewCustomerRequest {
    private String phone;
    private String name;
    public NewCustomerRequest() {}

    public NewCustomerRequest(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
