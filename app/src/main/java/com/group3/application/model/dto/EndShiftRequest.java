package com.group3.application.model.dto;

/**
 * Request DTO for ending a shift.
 * Matches the API endpoint: POST /shifts/end
 * API expects camelCase field: closingCash
 */
public class EndShiftRequest {
    
    private Double closingCash;

    // Constructors
    public EndShiftRequest() {}

    public EndShiftRequest(Double closingCash) {
        this.closingCash = closingCash;
    }

    // Getters and Setters
    public Double getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(Double closingCash) {
        this.closingCash = closingCash;
    }
}
