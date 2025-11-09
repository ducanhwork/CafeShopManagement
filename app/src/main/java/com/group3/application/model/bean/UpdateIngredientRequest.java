package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Request DTO for updating an existing ingredient
 */
public class UpdateIngredientRequest {
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("price")
    private Double price;
    
    @SerializedName("reorderLevel")
    private Integer reorderLevel;
    
    @SerializedName("imageLink")
    private String imageLink;
    
    @SerializedName("status")
    private String status;
    
    // Constructors
    public UpdateIngredientRequest() {
    }
    
    public UpdateIngredientRequest(String name, String description, Double price, Integer reorderLevel, String imageLink, String status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.reorderLevel = reorderLevel;
        this.imageLink = imageLink;
        this.status = status;
    }
    
    // Getters and Setters
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
    
    public Integer getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
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
}
