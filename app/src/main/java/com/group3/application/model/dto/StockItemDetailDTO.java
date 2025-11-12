package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;

public class StockItemDetailDTO {
    @SerializedName("itemName")
    private String itemName;
    @SerializedName("unit")
    private String unit;
    @SerializedName("stockQuantity")
    private Integer stockQuantity;
    public String getItemName() { return itemName; }
    public String getUnit() { return unit; }
    public Integer getStockQuantity() { return stockQuantity; }
}