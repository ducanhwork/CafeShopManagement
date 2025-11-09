package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * DTO for starting a new shift
 */
public class StartShiftRequest {
    
    @SerializedName("opening_cash")
    private BigDecimal openingCash;
    
    private String notes;

    public StartShiftRequest(BigDecimal openingCash, String notes) {
        this.openingCash = openingCash;
        this.notes = notes;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
