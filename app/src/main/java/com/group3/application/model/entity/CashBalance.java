package com.group3.application.model.entity;

import java.math.BigDecimal;

/**
 * CashBalance entity representing the current cash balance state for a shift.
 * Used by the /shifts/cash/balance endpoint.
 */
public class CashBalance {
    
    private String shiftId;
    
    private BigDecimal openingCash;
    
    private BigDecimal totalCashIn;
    
    private BigDecimal totalCashOut;
    
    private BigDecimal totalRefunds;
    
    private BigDecimal currentBalance;
    
    private BigDecimal expectedCash;
    
    private Integer transactionCount;

    // Constructors
    public CashBalance() {}

    public CashBalance(String shiftId, BigDecimal openingCash, BigDecimal totalCashIn,
                      BigDecimal totalCashOut, BigDecimal totalRefunds,
                      BigDecimal currentBalance, BigDecimal expectedCash,
                      Integer transactionCount) {
        this.shiftId = shiftId;
        this.openingCash = openingCash;
        this.totalCashIn = totalCashIn;
        this.totalCashOut = totalCashOut;
        this.totalRefunds = totalRefunds;
        this.currentBalance = currentBalance;
        this.expectedCash = expectedCash;
        this.transactionCount = transactionCount;
    }

    // Getters and Setters
    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public BigDecimal getTotalCashIn() {
        return totalCashIn;
    }

    public void setTotalCashIn(BigDecimal totalCashIn) {
        this.totalCashIn = totalCashIn;
    }

    public BigDecimal getTotalCashOut() {
        return totalCashOut;
    }

    public void setTotalCashOut(BigDecimal totalCashOut) {
        this.totalCashOut = totalCashOut;
    }

    public BigDecimal getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(BigDecimal totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(BigDecimal expectedCash) {
        this.expectedCash = expectedCash;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    /**
     * Calculate current discrepancy (current - expected)
     */
    public BigDecimal getDiscrepancy() {
        if (currentBalance == null || expectedCash == null) {
            return BigDecimal.ZERO;
        }
        return currentBalance.subtract(expectedCash);
    }

    /**
     * Check if there's a discrepancy
     */
    public boolean hasDiscrepancy() {
        return getDiscrepancy().compareTo(BigDecimal.ZERO) != 0;
    }
}
