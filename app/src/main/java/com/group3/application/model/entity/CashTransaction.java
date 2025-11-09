package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * CashTransaction entity representing a cash transaction during a shift.
 * Matches backend API response structure exactly.
 * 
 * Backend fields:
 * - id (String/UUID)
 * - shiftId (String/UUID)
 * - amount (BigDecimal/Double, must be > 0)
 * - transactionType (String: "CASH_IN", "CASH_OUT", "REFUND")
 * - description (String, optional)
 * - referenceNumber (String, optional - Order ID, Bill ID, etc.)
 * - timestamp (ISO 8601 format)
 */
public class CashTransaction {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("shiftId")
    private String shiftId;
    
    @SerializedName("amount")
    private Double amount;
    
    @SerializedName("transactionType")
    private String transactionType; // CASH_IN, CASH_OUT, REFUND
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("referenceNumber")
    private String referenceNumber; // e.g., ORD-123, BILL-456
    
    @SerializedName("timestamp")
    private String timestamp; // ISO 8601 format: "2025-11-09T10:30:00"

    // Constructors
    public CashTransaction() {}

    public CashTransaction(String id, String shiftId, Double amount, 
                          String transactionType, String description, 
                          String referenceNumber, String timestamp) {
        this.id = id;
        this.shiftId = shiftId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.referenceNumber = referenceNumber;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Helper Methods

    /**
     * Check if transaction is a cash in
     */
    public boolean isCashIn() {
        return "CASH_IN".equalsIgnoreCase(transactionType);
    }

    /**
     * Check if transaction is a cash out
     */
    public boolean isCashOut() {
        return "CASH_OUT".equalsIgnoreCase(transactionType);
    }

    /**
     * Check if transaction is a refund
     */
    public boolean isRefund() {
        return "REFUND".equalsIgnoreCase(transactionType);
    }

    /**
     * Get signed amount for cash balance calculation
     * CASH_IN: positive, CASH_OUT/REFUND: negative
     */
    public Double getSignedAmount() {
        if (amount == null) {
            return 0.0;
        }
        if (isCashIn()) {
            return amount;
        } else {
            return -amount;
        }
    }

    /**
     * Get transaction type display text
     */
    public String getTransactionTypeDisplay() {
        if (transactionType == null) {
            return "Unknown";
        }
        switch (transactionType.toUpperCase()) {
            case "CASH_IN":
                return "Cash In";
            case "CASH_OUT":
                return "Cash Out";
            case "REFUND":
                return "Refund";
            default:
                return transactionType;
        }
    }
}
