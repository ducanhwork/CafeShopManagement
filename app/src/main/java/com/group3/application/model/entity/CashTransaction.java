package com.group3.application.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class CashTransaction implements Serializable {
    private UUID id;
    private UUID shiftId;
    private Double amount;
    private String transactionType; // CASH_IN, CASH_OUT, REFUND
    private String description;
    private String referenceNumber;
    private LocalDateTime timestamp;
    private Double runningBalance;

    public CashTransaction() {
    }

    public CashTransaction(UUID id, UUID shiftId, Double amount, String transactionType,
                           String description, String referenceNumber, LocalDateTime timestamp,
                           Double runningBalance) {
        this.id = id;
        this.shiftId = shiftId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.referenceNumber = referenceNumber;
        this.timestamp = timestamp;
        this.runningBalance = runningBalance;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getShiftId() {
        return shiftId;
    }

    public void setShiftId(UUID shiftId) {
        this.shiftId = shiftId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getRunningBalance() {
        return runningBalance;
    }

    public void setRunningBalance(Double runningBalance) {
        this.runningBalance = runningBalance;
    }
}
