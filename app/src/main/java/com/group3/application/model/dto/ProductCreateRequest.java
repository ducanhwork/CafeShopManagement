package com.group3.application.model.dto;


import java.math.BigDecimal;


public class ProductCreateRequest {

    private String name;

    private String description;

    private Double price;

    private String categoryName;

    public ProductCreateRequest(String name, String description, Double price, String categoryName) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryName = categoryName;
    }

    public ProductCreateRequest() {
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
