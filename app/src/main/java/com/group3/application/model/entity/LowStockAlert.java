package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Low stock alert entity for ingredients below reorder level
 */
public class LowStockAlert {
    
    @SerializedName("productId")
    private String productId;
    
    @SerializedName("productName")
    private String productName;
    
    @SerializedName("currentStock")
    private Integer currentStock;
    
    @SerializedName("reorderLevel")
    private Integer reorderLevel;
    
    @SerializedName("deficit")
    private Integer deficit;
    
    @SerializedName("status")
    private String status;
    
    // Status constants
    public static final String STATUS_LOW_STOCK = "LOW_STOCK";
    public static final String STATUS_OUT_OF_STOCK = "OUT_OF_STOCK";
    
    // Constructors
    public LowStockAlert() {
    }
    
    // Getters and Setters
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
    
    public Integer getDeficit() {
        return deficit;
    }
    
    public void setDeficit(Integer deficit) {
        this.deficit = deficit;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Helper methods
    public boolean isOutOfStock() {
        return STATUS_OUT_OF_STOCK.equals(status) || (currentStock != null && currentStock == 0);
    }
    
    public boolean isLowStock() {
        return STATUS_LOW_STOCK.equals(status);
    }
    
    public int getSafeDeficit() {
        return deficit != null ? deficit : 0;
    }
    
    public int getSafeCurrentStock() {
        return currentStock != null ? currentStock : 0;
    }
    
    public int getSafeReorderLevel() {
        return reorderLevel != null ? reorderLevel : 0;
    }
    
    @Override
    public String toString() {
        return "LowStockAlert{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", currentStock=" + currentStock +
                ", reorderLevel=" + reorderLevel +
                ", deficit=" + deficit +
                ", status='" + status + '\'' +
                '}';
    }
}
