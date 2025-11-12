package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StockReportDTO {
    @SerializedName("totalItems")
    private Long totalItems;

    @SerializedName("lowStockItems")
    private Long lowStockItems;

    @SerializedName("details")
    private List<StockItemDetailDTO> details;

    public Long getTotalItems() { return totalItems; }
    public Long getLowStockItems() { return lowStockItems; }
    public List<StockItemDetailDTO> getDetails() { return details; }
}