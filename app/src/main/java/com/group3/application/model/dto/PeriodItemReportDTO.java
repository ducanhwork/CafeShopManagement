package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PeriodItemReportDTO {
    @SerializedName("periodLabel")
    private String periodLabel;

    @SerializedName("topItems")
    private List<ItemReportDetailDTO> topItems;

    @SerializedName("bottomItems")
    private List<ItemReportDetailDTO> bottomItems;

    // --- Getters ---
    public String getPeriodLabel() { return periodLabel; }
    public List<ItemReportDetailDTO> getTopItems() { return topItems; }
    public List<ItemReportDetailDTO> getBottomItems() { return bottomItems; }
}