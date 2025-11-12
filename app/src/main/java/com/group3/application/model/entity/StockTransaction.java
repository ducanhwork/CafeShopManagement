package com.group3.application.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class StockTransaction implements Serializable {
    private UUID id;
    private UUID productId;
    private String productName;
    private Integer quantity;
    private String transactionType; // INCOMING, OUTGOING, ADJUSTMENT
    private LocalDateTime transactionTime;
    private String userName;
    private Integer stockLevelAfter;
    private String notes;

    public StockTransaction() {
    }

    public StockTransaction(UUID id, UUID productId, String productName, Integer quantity,
                            String transactionType, LocalDateTime transactionTime, String userName,
                            Integer stockLevelAfter, String notes) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.transactionTime = transactionTime;
        this.userName = userName;
        this.stockLevelAfter = stockLevelAfter;
        this.notes = notes;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getStockLevelAfter() {
        return stockLevelAfter;
    }

    public void setStockLevelAfter(Integer stockLevelAfter) {
        this.stockLevelAfter = stockLevelAfter;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
