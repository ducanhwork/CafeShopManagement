package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class ItemReportDetailDTO {
    @SerializedName("itemName")
    private String itemName;

    @SerializedName("totalUnit")
    private long totalUnit;

    @SerializedName("totalRevenue")
    private BigDecimal totalRevenue;

    public String getItemName() { return itemName; }
    public long getTotalUnit() { return totalUnit; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}