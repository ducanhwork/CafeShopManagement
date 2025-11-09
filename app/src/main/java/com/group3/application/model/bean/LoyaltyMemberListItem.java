package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class LoyaltyMemberListItem {
    @SerializedName("customerId")
    private UUID id;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @SerializedName("email")
    private String email;

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("points")
    private Integer points;

    @SerializedName("tier")
    private String tier;

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public Integer getPoints() { return points; }
    public String getTier() { return tier; }
}
