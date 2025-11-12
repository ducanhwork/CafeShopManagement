package com.group3.application.model.dto;

public class IngredientRequest {
    private String name;
    private String description;
    private Double price;
    private Integer reorderLevel;
    private String unit; // kg, liter, pieces, grams, etc.
    private String imageLink;
    private String status;

    public IngredientRequest() {
    }

    public IngredientRequest(String name, String description, Double price, Integer reorderLevel,
                             String unit, String imageLink, String status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.reorderLevel = reorderLevel;
        this.unit = unit;
        this.imageLink = imageLink;
        this.status = status;
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

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
