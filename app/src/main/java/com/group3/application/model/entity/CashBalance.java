package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * CashBalance entity representing the current cash balance state for a shift.
 * Matches backend API response from /shifts/cash/balance endpoint.
 * 
 * Backend fields:
 * - shiftId (String/UUID)
 * - openingCash (BigDecimal/Double)
 * - totalCashIn (BigDecimal/Double)
 * - totalCashOut (BigDecimal/Double)
 * - totalRefunds (BigDecimal/Double)
 * - expectedBalance (BigDecimal/Double) - formula: openingCash + totalCashIn - totalCashOut - totalRefunds
 * - transactionCount (Integer)
 */
public class CashBalance {
    
    @SerializedName("shiftId")
    private String shiftId;
    
    @SerializedName("openingCash")
    private Double openingCash;
    
    @SerializedName("totalCashIn")
    private Double totalCashIn;
    
    @SerializedName("totalCashOut")
    private Double totalCashOut;
    
    @SerializedName("totalRefunds")
    private Double totalRefunds;
    
    @SerializedName("expectedBalance")
    private Double expectedBalance; // openingCash + totalCashIn - totalCashOut - totalRefunds
    
    @SerializedName("transactionCount")
    private Integer transactionCount;

    // Constructors
    public CashBalance() {}

    public CashBalance(String shiftId, Double openingCash, Double totalCashIn,
                      Double totalCashOut, Double totalRefunds,
                      Double expectedBalance, Integer transactionCount) {
        this.shiftId = shiftId;
        this.openingCash = openingCash;
        this.totalCashIn = totalCashIn;
        this.totalCashOut = totalCashOut;
        this.totalRefunds = totalRefunds;
        this.expectedBalance = expectedBalance;
        this.transactionCount = transactionCount;
    }

    // Getters and Setters
    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public Double getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(Double openingCash) {
        this.openingCash = openingCash;
    }

    public Double getTotalCashIn() {
        return totalCashIn;
    }

    public void setTotalCashIn(Double totalCashIn) {
        this.totalCashIn = totalCashIn;
    }

    public Double getTotalCashOut() {
        return totalCashOut;
    }

    public void setTotalCashOut(Double totalCashOut) {
        this.totalCashOut = totalCashOut;
    }

    public Double getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(Double totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    public Double getExpectedBalance() {
        return expectedBalance;
    }

    public void setExpectedBalance(Double expectedBalance) {
        this.expectedBalance = expectedBalance;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    // Helper Methods

    /**
     * Get total net cash flow (cashIn - cashOut - refunds)
     */
    public Double getTotalNetCashFlow() {
        double cashIn = totalCashIn != null ? totalCashIn : 0.0;
        double cashOut = totalCashOut != null ? totalCashOut : 0.0;
        double refunds = totalRefunds != null ? totalRefunds : 0.0;
        return cashIn - cashOut - refunds;
    }

    /**
     * Check if balance has been calculated
     */
    public boolean hasBalance() {
        return expectedBalance != null;
    }
}
