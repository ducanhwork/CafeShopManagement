package com.group3.application.model.dto;

public class CashTransactionRequest {
    private Double amount;
    private String transactionType; // CASH_IN, CASH_OUT, REFUND
    private String description;
    private String referenceNumber;

    public CashTransactionRequest() {
    }

    public CashTransactionRequest(Double amount, String transactionType, String description, String referenceNumber) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.referenceNumber = referenceNumber;
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
}
