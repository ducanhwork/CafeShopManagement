package com.group3.application.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Shift implements Serializable {
    private UUID id;
    private UUID userId;
    private String userFullName;
    private String userEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double openingCash;
    private Double closingCash;
    private String status; // OPEN, CLOSED
    private Integer durationMinutes;
    private Double cashDiscrepancy;

    public Shift() {
    }

    public Shift(UUID id, UUID userId, String userFullName, String userEmail, LocalDateTime startTime,
                 LocalDateTime endTime, Double openingCash, Double closingCash, String status,
                 Integer durationMinutes, Double cashDiscrepancy) {
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
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getCashDiscrepancy() {
        return cashDiscrepancy;
    }

    public void setCashDiscrepancy(Double cashDiscrepancy) {
        this.cashDiscrepancy = cashDiscrepancy;
    }
}
