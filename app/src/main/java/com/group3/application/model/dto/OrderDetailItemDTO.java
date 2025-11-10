package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;

public class OrderDetailItemDTO {
    @SerializedName("productId")
    private String productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private double price;

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
