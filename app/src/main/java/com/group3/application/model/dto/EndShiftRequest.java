package com.group3.application.model.dto;

public class EndShiftRequest {
    private Double closingCash;

    public EndShiftRequest() {
    }

    public EndShiftRequest(Double closingCash) {
        this.closingCash = closingCash;
    }

    public Double getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(Double closingCash) {
        this.closingCash = closingCash;
    }
}
