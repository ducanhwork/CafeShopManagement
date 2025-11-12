package com.group3.application.model.dto;

import java.util.UUID;

public class IncomingStockRequest {
    private UUID productId;
    private Integer quantity;
    private String transactionType; // INCOMING, OUTGOING, ADJUSTMENT
    private String notes;

    public IncomingStockRequest() {
    }

    public IncomingStockRequest(UUID productId, Integer quantity, String transactionType, String notes) {
        this.productId = productId;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.notes = notes;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
