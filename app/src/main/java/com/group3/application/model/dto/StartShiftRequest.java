package com.group3.application.model.dto;

/**
 * Request DTO for starting a new shift.
 * Matches the API endpoint: POST /shifts/start
 * API expects camelCase field: openingCash
 */
public class StartShiftRequest {
    
    private Double openingCash;

    // Constructors
    public StartShiftRequest() {}

    public StartShiftRequest(Double openingCash) {
        this.openingCash = openingCash;
    }

    // Getters and Setters
    public Double getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(Double openingCash) {
        this.openingCash = openingCash;
    }
}
