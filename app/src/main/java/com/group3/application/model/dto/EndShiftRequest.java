package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * DTO for ending a shift
 */
public class EndShiftRequest {
    
    @SerializedName("closing_cash")
    private BigDecimal closingCash;
    
    @SerializedName("actual_cash")
    private BigDecimal actualCash;
    
    private String notes;

    public EndShiftRequest(BigDecimal closingCash, BigDecimal actualCash, String notes) {
        this.closingCash = closingCash;
        this.actualCash = actualCash;
        this.notes = notes;
    }

    public BigDecimal getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(BigDecimal closingCash) {
        this.closingCash = closingCash;
    }

    public BigDecimal getActualCash() {
        return actualCash;
    }

    public void setActualCash(BigDecimal actualCash) {
        this.actualCash = actualCash;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
