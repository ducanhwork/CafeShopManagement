package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Ingredient entity representing coffee shop supplies/ingredients
 * Stored as Product with category='Ingredient' on backend
 */
public class Ingredient {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("price")
    private Double price;
    
    @SerializedName("imageLink")
    private String imageLink;
    
    @SerializedName("status")
    private String status; // "active" or "inactive"
    
    @SerializedName("categoryName")
    private String categoryName; // Always "Ingredient"
    
    @SerializedName("quantityInStock")
    private Integer quantityInStock;
    
    @SerializedName("reorderLevel")
    private Integer reorderLevel;
    
    @SerializedName("isLowStock")
    private Boolean isLowStock;
    
    // Status constants
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    
    // Constructors
    public Ingredient() {
    }
    
    public Ingredient(String name, String description, Double price, Integer reorderLevel, String imageLink, String status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.reorderLevel = reorderLevel;
        this.imageLink = imageLink;
        this.status = status;
        this.quantityInStock = 0;
        this.isLowStock = true;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getImageLink() {
        return imageLink;
    }
    
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public Integer getQuantityInStock() {
        return quantityInStock != null ? quantityInStock : 0;
    }
    
    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
    
    public Integer getReorderLevel() {
        return reorderLevel != null ? reorderLevel : 0;
    }
    
    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    public Boolean getIsLowStock() {
        return isLowStock != null ? isLowStock : false;
    }
    
    public void setIsLowStock(Boolean lowStock) {
        isLowStock = lowStock;
    }
    
    // Helper methods
    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }
    
    public boolean isInactive() {
        return STATUS_INACTIVE.equals(status);
    }
    
    public boolean isLowStock() {
        return getIsLowStock();
    }
    
    public boolean isOutOfStock() {
        return getQuantityInStock() == 0;
    }
    
    public int getStockDeficit() {
        if (isLowStock()) {
            return Math.max(0, getReorderLevel() - getQuantityInStock());
        }
        return 0;
    }
    
    public double getTotalStockValue() {
        return getQuantityInStock() * (price != null ? price : 0.0);
    }
    
    /**
     * Validate ingredient data
     */
    public boolean isValid() {
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            return false;
        }
        if (description != null && description.length() > 500) {
            return false;
        }
        if (price == null || price <= 0) {
            return false;
        }
        if (reorderLevel != null && reorderLevel < 0) {
            return false;
        }
        if (imageLink != null && imageLink.length() > 255) {
            return false;
        }
        if (status != null && !STATUS_ACTIVE.equals(status) && !STATUS_INACTIVE.equals(status)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Ingredient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", quantityInStock=" + quantityInStock +
                ", reorderLevel=" + reorderLevel +
                ", isLowStock=" + isLowStock +
                ", status='" + status + '\'' +
                '}';
    }
}
