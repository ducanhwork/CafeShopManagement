package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class LoyaltyMemberDetailResponse {
    @SerializedName("customerId")
    private UUID id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("loyaltyId")
    private String loyaltyId;

    @SerializedName("points")
    private Integer points;

    @SerializedName("tier")
    private String tier;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getLoyaltyId() {
        return loyaltyId;
    }

    public void setLoyaltyId(String loyaltyId) {
        this.loyaltyId = loyaltyId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public Integer getPoints() { return points; }
    public String getTier() { return tier; }
}
