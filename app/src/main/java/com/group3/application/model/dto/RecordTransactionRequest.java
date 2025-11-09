package com.group3.application.model.dto;

import java.math.BigDecimal;

/**
 * Request DTO for recording a cash transaction during a shift.
 * Matches the API endpoint: POST /shifts/cash/record
 * API expects camelCase fields
 */
public class RecordTransactionRequest {
    
    private BigDecimal amount;
    
    private String transactionType; // CASH_IN, CASH_OUT, REFUND
    
    private String description;
    
    private String referenceNumber; // Optional, e.g., ORD-123

    // Constructors
    public RecordTransactionRequest() {}

    public RecordTransactionRequest(BigDecimal amount, String transactionType, 
                                   String description, String referenceNumber) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.referenceNumber = referenceNumber;
    }

    // Getters and Setters
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
}
