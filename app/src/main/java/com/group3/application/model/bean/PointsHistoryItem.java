package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.util.UUID;

public class PointsHistoryItem {

    @SerializedName("transactionId")
    private UUID id;

    @SerializedName("orderId")
    private UUID orderId;

    @SerializedName("orderNo")
    private String orderNo;

    @SerializedName("type")
    private String type; // "EARN" hoặc "REDEEM"

    @SerializedName("pointsEarned")
    private Integer pointsEarned;

    @SerializedName("pointsSpent")
    private Integer pointsSpent;

    // Map "timestamp" từ JSON vào biến "transactionDate"
    @SerializedName("timestamp")
    private LocalDateTime transactionDate;

    // Getters
    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public String getOrderNo() { return orderNo; }
    public String getType() { return type; }
    public Integer getPointsEarned() { return pointsEarned; }
    public Integer getPointsSpent() { return pointsSpent; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
}
