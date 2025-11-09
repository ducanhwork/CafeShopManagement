package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Shift entity representing a work shift with cash management.
 * Matches the backend API response structure exactly.
 * 
 * Backend fields:
 * - id (String/UUID)
 * - userId (String/UUID)
 * - userFullName (String)
 * - userEmail (String)
 * - startTime (ISO 8601 format)
 * - endTime (ISO 8601 format, null if OPEN)
 * - openingCash (BigDecimal/Double)
 * - closingCash (BigDecimal/Double, null if OPEN)
 * - status (String: "OPEN" or "CLOSED")
 * - durationMinutes (Long, null if OPEN)
 * - cashDiscrepancy (BigDecimal/Double, null if OPEN)
 */
public class Shift {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("userFullName")
    private String userFullName;
    
    @SerializedName("userEmail")
    private String userEmail;
    
    @SerializedName("startTime")
    private String startTime; // ISO 8601 format: "2025-11-09T08:00:00"
    
    @SerializedName("endTime")
    private String endTime; // Null if shift is still OPEN
    
    @SerializedName("openingCash")
    private Double openingCash;
    
    @SerializedName("closingCash")
    private Double closingCash; // Null if shift is still OPEN
    
    @SerializedName("status")
    private String status; // "OPEN" or "CLOSED"
    
    @SerializedName("durationMinutes")
    private Long durationMinutes; // Null if shift is still OPEN
    
    @SerializedName("cashDiscrepancy")
    private Double cashDiscrepancy; // Null if shift is still OPEN, formula: closingCash - (openingCash + totalCashIn - totalCashOut)

    // Constructors
    public Shift() {}

    public Shift(String id, String userId, String userFullName, String userEmail,
                 String startTime, String endTime, Double openingCash, Double closingCash,
                 String status, Long durationMinutes, Double cashDiscrepancy) {
        this.id = id;
        this.userId = userId;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.openingCash = openingCash;
        this.closingCash = closingCash;
        this.status = status;
        this.durationMinutes = durationMinutes;
        this.cashDiscrepancy = cashDiscrepancy;
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

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public Double getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(Double openingCash) {
        this.openingCash = openingCash;
    }

    public Double getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(Double closingCash) {
        this.closingCash = closingCash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getCashDiscrepancy() {
        return cashDiscrepancy;
    }

    public void setCashDiscrepancy(Double cashDiscrepancy) {
        this.cashDiscrepancy = cashDiscrepancy;
    }

    // Helper Methods

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

    /**
     * Get formatted duration string (e.g., "8h 30m")
     */
    public String getFormattedDuration() {
        if (durationMinutes == null || durationMinutes == 0) {
            return "N/A";
        }
        long hours = durationMinutes / 60;
        long minutes = durationMinutes % 60;
        return String.format("%dh %dm", hours, minutes);
    }

    /**
     * Get discrepancy level for UI color coding
     * @return 0 for zero/positive (green), 1 for minor negative (orange), 2 for major negative (red)
     */
    public int getDiscrepancyLevel() {
        if (cashDiscrepancy == null) {
            return 0; // No discrepancy yet - green
        }
        if (cashDiscrepancy >= 0) {
            return 0; // Zero or positive - green
        } else if (cashDiscrepancy >= -50.0) {
            return 1; // Minor negative (<$50) - orange
        } else {
            return 2; // Major negative (>=$50) - red
        }
    }

    /**
     * Check if there is a cash discrepancy
     */
    public boolean hasDiscrepancy() {
        return cashDiscrepancy != null && Math.abs(cashDiscrepancy) > 0.01;
    }

    /**
     * Get user display name (full name or email as fallback)
     */
    public String getUserDisplayName() {
        if (userFullName != null && !userFullName.trim().isEmpty()) {
            return userFullName;
        }
        return userEmail != null ? userEmail : "Unknown User";
    }
}
