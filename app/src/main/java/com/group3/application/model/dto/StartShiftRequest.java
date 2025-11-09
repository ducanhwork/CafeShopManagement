package com.group3.application.model.dto;

import java.math.BigDecimal;

/**
 * Request DTO for starting a new shift.
 * Matches the API endpoint: POST /shifts/start
 * API expects camelCase field: openingCash
 */
public class StartShiftRequest {
    
    private BigDecimal openingCash;

    // Constructors
    public StartShiftRequest() {}

    public StartShiftRequest(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    // Getters and Setters
    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }
}
