package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class RevenueDetailDTO {
    @SerializedName("timeLabel")
    private String date;

    @SerializedName("totalRevenue")
    private BigDecimal totalRevenue;

    @SerializedName("billCount")
    private long billCount;

    public String getDate() { return date; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public long getBillCount() { return billCount; }
}