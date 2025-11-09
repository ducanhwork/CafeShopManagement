package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Request DTO for adding stock transaction
 * Used for POST /api/inventory/stock/incoming endpoint
 */
public class AddStockRequest {
    
    @SerializedName("productId")
    private String productId;
    
    @SerializedName("quantity")
    private Integer quantity;
    
    // Constructors
    public AddStockRequest() {
    }
    
    public AddStockRequest(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    // Legacy constructor for backward compatibility
    @Deprecated
    public AddStockRequest(String productId, Integer quantity, String transactionType, String notes) {
        this.productId = productId;
        this.quantity = quantity;
        // transactionType and notes are ignored - /incoming endpoint doesn't use them
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
}
