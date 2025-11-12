package com.group3.application.model.dto;

public class CashBalanceResponse {
    private Double openingCash;
    private Double totalCashIn;
    private Double totalCashOut;
    private Double totalRefunds;
    private Double expectedBalance;
    private Double currentBalance;
    private Integer transactionCount;

    public CashBalanceResponse() {
    }

    public CashBalanceResponse(Double openingCash, Double totalCashIn, Double totalCashOut,
                               Double totalRefunds, Double expectedBalance, Double currentBalance,
                               Integer transactionCount) {
        this.openingCash = openingCash;
        this.totalCashIn = totalCashIn;
        this.totalCashOut = totalCashOut;
        this.totalRefunds = totalRefunds;
        this.expectedBalance = expectedBalance;
        this.currentBalance = currentBalance;
        this.transactionCount = transactionCount;
    }

    // Getters and Setters
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

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }
}
