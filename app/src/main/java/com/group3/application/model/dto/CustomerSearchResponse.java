package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class CustomerSearchResponse {
    private UUID customerId;
    private String name;
    private String phone;
    @SerializedName("points")
    private int availablePoints;

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getAvailablePoints() { return availablePoints; }
    public void setAvailablePoints(int availablePoints) { this.availablePoints = availablePoints; }
}
