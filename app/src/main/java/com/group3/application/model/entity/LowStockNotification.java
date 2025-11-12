package com.group3.application.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class LowStockNotification implements Serializable {
    private UUID productId;
    private String productName;
    private Integer currentStock;
    private Integer reorderLevel;
    private Integer quantityNeeded;
    private Double estimatedCost;
    private String status; // CRITICAL, LOW
    private String message;
    private Date timestamp;

    public LowStockNotification() {
    }

    public LowStockNotification(UUID productId, String productName, Integer currentStock,
                                Integer reorderLevel, Integer quantityNeeded, Double estimatedCost,
                                String status) {
        this.productId = productId;
        this.productName = productName;
        this.currentStock = currentStock;
        this.reorderLevel = reorderLevel;
        this.quantityNeeded = quantityNeeded;
        this.estimatedCost = estimatedCost;
        this.status = status;
    }

    // Getters and Setters
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

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Integer getQuantityNeeded() {
        return quantityNeeded;
    }

    public void setQuantityNeeded(Integer quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // Convenience methods for adapters
    public UUID getIngredientId() {
        return productId;
    }

    public String getIngredientName() {
        return productName;
    }
}
