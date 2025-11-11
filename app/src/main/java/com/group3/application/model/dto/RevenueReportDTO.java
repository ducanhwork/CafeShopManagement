package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class RevenueReportDTO {
    @SerializedName("totalRevenue")
    private BigDecimal totalRevenue;

    @SerializedName("totalBills")
    private long totalBills;

    @SerializedName("avgRevenuePerBill")
    private BigDecimal avgRevenuePerBill;

    @SerializedName("details")
    private List<RevenueDetailDTO> details;

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public long getTotalBills() { return totalBills; }
    public BigDecimal getAvgRevenuePerBill() { return avgRevenuePerBill; }
    public List<RevenueDetailDTO> getDetails() { return details; }
}