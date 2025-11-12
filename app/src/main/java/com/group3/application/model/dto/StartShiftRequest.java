package com.group3.application.model.dto;

public class StartShiftRequest {
    private Double openingCash;

    public StartShiftRequest() {
    }

    public StartShiftRequest(Double openingCash) {
        this.openingCash = openingCash;
    }

    public Double getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(Double openingCash) {
        this.openingCash = openingCash;
    }
}
