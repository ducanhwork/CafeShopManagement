package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Stock transaction entity for tracking inventory movements
 */
public class StockTransaction {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("productId")
    private String productId;
    
    @SerializedName("productName")
    private String productName;
    
    @SerializedName("quantity")
    private Integer quantity;
    
    @SerializedName("transactionType")
    private String transactionType;
    
    @SerializedName("notes")
    private String notes;
    
    @SerializedName("timestamp")
    private String timestamp;
    
    @SerializedName("performedBy")
    private String performedBy;
    
    // Transaction type constants
    public static final String TYPE_INCOMING = "INCOMING";
    public static final String TYPE_OUTGOING = "OUTGOING";
    public static final String TYPE_ADJUSTMENT = "ADJUSTMENT";
    
    // Constructors
    public StockTransaction() {
    }
    
    public StockTransaction(String productId, Integer quantity, String transactionType, String notes) {
        this.productId = productId;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
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
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPerformedBy() {
        return performedBy;
    }
    
    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }
    
    // Helper methods
    public boolean isIncoming() {
        return TYPE_INCOMING.equals(transactionType);
    }
    
    public boolean isOutgoing() {
        return TYPE_OUTGOING.equals(transactionType);
    }
    
    public boolean isAdjustment() {
        return TYPE_ADJUSTMENT.equals(transactionType);
    }
    
    /**
     * Get quantity with sign prefix for display
     */
    public String getSignedQuantity() {
        if (isIncoming()) {
            return "+" + quantity;
        } else if (isOutgoing()) {
            return "-" + quantity;
        } else {
            return quantity >= 0 ? "+" + quantity : String.valueOf(quantity);
        }
    }
    
    /**
     * Validate transaction data
     */
    public boolean isValid() {
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }
        if (quantity == null || quantity < 1 || quantity > 10000) {
            return false;
        }
        if (transactionType == null || 
            (!TYPE_INCOMING.equals(transactionType) && 
             !TYPE_OUTGOING.equals(transactionType) && 
             !TYPE_ADJUSTMENT.equals(transactionType))) {
            return false;
        }
        if (notes != null && notes.length() > 500) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "StockTransaction{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", transactionType='" + transactionType + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
