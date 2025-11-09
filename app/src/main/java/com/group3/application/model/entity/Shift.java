package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Shift entity representing a work shift with cash management
 */
public class Shift {
    
    private String id;
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("user_name")
    private String userName;
    
    @SerializedName("start_time")
    private String startTime;
    
    @SerializedName("end_time")
    private String endTime;
    
    @SerializedName("opening_cash")
    private BigDecimal openingCash;
    
    @SerializedName("closing_cash")
    private BigDecimal closingCash;
    
    @SerializedName("expected_cash")
    private BigDecimal expectedCash;
    
    @SerializedName("actual_cash")
    private BigDecimal actualCash;
    
    @SerializedName("cash_difference")
    private BigDecimal cashDifference;
    
    private String notes;
    
    @SerializedName("status")
    private String status; // OPEN, CLOSED
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;

    // Constructors
    public Shift() {}

    public Shift(String id, String userId, String userName, String startTime, String endTime,
                 BigDecimal openingCash, BigDecimal closingCash, BigDecimal expectedCash,
                 BigDecimal actualCash, BigDecimal cashDifference, String notes, String status) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.openingCash = openingCash;
        this.closingCash = closingCash;
        this.expectedCash = expectedCash;
        this.actualCash = actualCash;
        this.cashDifference = cashDifference;
        this.notes = notes;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public BigDecimal getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(BigDecimal closingCash) {
        this.closingCash = closingCash;
    }

    public BigDecimal getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(BigDecimal expectedCash) {
        this.expectedCash = expectedCash;
    }

    public BigDecimal getActualCash() {
        return actualCash;
    }

    public void setActualCash(BigDecimal actualCash) {
        this.actualCash = actualCash;
    }

    public BigDecimal getCashDifference() {
        return cashDifference;
    }

    public void setCashDifference(BigDecimal cashDifference) {
        this.cashDifference = cashDifference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Check if shift is currently open
     */
    public boolean isOpen() {
        return "OPEN".equalsIgnoreCase(status);
    }

    /**
     * Check if shift is closed
     */
    public boolean isClosed() {
        return "CLOSED".equalsIgnoreCase(status);
    }
}
