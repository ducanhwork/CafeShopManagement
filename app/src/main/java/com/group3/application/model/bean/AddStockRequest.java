package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Request DTO for adding stock transaction
 */
public class AddStockRequest {
    
    @SerializedName("productId")
    private String productId;
    
    @SerializedName("quantity")
    private Integer quantity;
    
    @SerializedName("transactionType")
    private String transactionType;
    
    @SerializedName("notes")
    private String notes;
    
    // Constructors
    public AddStockRequest() {
    }
    
    public AddStockRequest(String productId, Integer quantity, String transactionType, String notes) {
        this.productId = productId;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
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
