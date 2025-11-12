package com.group3.application.model.entity;

import java.io.Serializable;
import java.util.UUID;

public class Ingredient implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private String unit; // kg, liters, pieces, etc.
    private Double price;
    private String imageLink;
    private String status; // active, inactive
    private String categoryName;
    private Integer quantityInStock;
    private Integer reorderLevel;
    private Boolean isLowStock;

    public Ingredient() {
    }

    public Ingredient(UUID id, String name, String description, String unit, Double price, String imageLink,
                      String status, String categoryName, Integer quantityInStock,
                      Integer reorderLevel, Boolean isLowStock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.price = price;
        this.imageLink = imageLink;
        this.status = status;
        this.categoryName = categoryName;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
        this.isLowStock = isLowStock;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Boolean getIsLowStock() {
        return isLowStock;
    }

    public void setIsLowStock(Boolean lowStock) {
        isLowStock = lowStock;
    }

    // Convenience method for boolean check
    public boolean isLowStock() {
        return Boolean.TRUE.equals(isLowStock);
    }

    // Helper method to display quantity with unit (e.g., "75 kg" or "50 liter")
    public String getQuantityWithUnit() {
        if (unit != null && !unit.isEmpty()) {
            return quantityInStock + " " + unit;
        }
        return String.valueOf(quantityInStock);
    }
}
