package com.group3.application.model.dto;

import java.math.BigDecimal;

/**
 * Request DTO for ending a shift.
 * Matches the API endpoint: POST /shifts/end
 * API expects camelCase field: closingCash
 */
public class EndShiftRequest {
    
    private BigDecimal closingCash;

    // Constructors
    public EndShiftRequest() {}

    public EndShiftRequest(BigDecimal closingCash) {
        this.closingCash = closingCash;
    }

    // Getters and Setters
    public BigDecimal getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(BigDecimal closingCash) {
        this.closingCash = closingCash;
    }
}
