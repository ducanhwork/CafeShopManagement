package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Shift entity representing a work shift with cash management.
 * Matches the backend API response structure with camelCase field names.
 */
public class Shift {
    
    private String id;
    
    private String userId;
    
    private String userName;
    
    private String startTime; // ISO 8601 format: "2025-11-09T08:00:00"
    
    private String endTime;
    
    private BigDecimal openingCash;
    
    private BigDecimal closingCash;
    
    private BigDecimal expectedCash;
    
    private BigDecimal discrepancy; // closingCash - expectedCash
    
    private Integer duration; // Duration in minutes (for closed shifts)
    
    private String status; // OPEN or CLOSED

    // Constructors
    public Shift() {}

    public Shift(String id, String userId, String userName, String startTime, String endTime,
                 BigDecimal openingCash, BigDecimal closingCash, BigDecimal expectedCash,
                 BigDecimal discrepancy, Integer duration, String status) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.openingCash = openingCash;
        this.closingCash = closingCash;
        this.expectedCash = expectedCash;
        this.discrepancy = discrepancy;
        this.duration = duration;
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

    public BigDecimal getDiscrepancy() {
        return discrepancy;
    }

    public void setDiscrepancy(BigDecimal discrepancy) {
        this.discrepancy = discrepancy;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    /**
     * Get formatted duration string (e.g., "8h 30m")
     */
    public String getFormattedDuration() {
        if (duration == null || duration == 0) {
            return "N/A";
        }
        int hours = duration / 60;
        int minutes = duration % 60;
        return String.format("%dh %dm", hours, minutes);
    }

    /**
     * Get discrepancy color code for UI
     * @return 0 for no discrepancy (green), 1 for minor (<50k, yellow), 2 for major (>=50k, red)
     */
    public int getDiscrepancyLevel() {
        if (discrepancy == null) {
            return 0;
        }
        BigDecimal absDiscrepancy = discrepancy.abs();
        if (absDiscrepancy.compareTo(BigDecimal.ZERO) == 0) {
            return 0; // Perfect - green
        } else if (absDiscrepancy.compareTo(new BigDecimal("50000")) < 0) {
            return 1; // Minor - yellow
        } else {
            return 2; // Major - red
        }
    }
}
