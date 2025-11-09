package com.group3.application.model.entity;

import java.math.BigDecimal;

/**
 * CashTransaction entity representing a cash transaction during a shift.
 * Used for tracking individual cash movements (IN/OUT/REFUND).
 */
public class CashTransaction {
    
    private String id;
    
    private String shiftId;
    
    private BigDecimal amount;
    
    private String transactionType; // CASH_IN, CASH_OUT, REFUND
    
    private String description;
    
    private String referenceNumber; // e.g., ORD-123
    
    private String timestamp; // ISO 8601 format
    
    private BigDecimal runningBalance; // Balance after this transaction

    // Constructors
    public CashTransaction() {}

    public CashTransaction(String id, String shiftId, BigDecimal amount, 
                          String transactionType, String description, 
                          String referenceNumber, String timestamp, 
                          BigDecimal runningBalance) {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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

    public BigDecimal getRunningBalance() {
        return runningBalance;
    }

    public void setRunningBalance(BigDecimal runningBalance) {
        this.runningBalance = runningBalance;
    }

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
}
