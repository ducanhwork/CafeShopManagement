package com.group3.application.model.dto;

import java.math.BigDecimal;

public class BillCalculationResponse {
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal finalTotal;
    private String customerName;
    private int customerAvailablePoints;

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getFinalTotal() { return finalTotal; }
    public void setFinalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getCustomerAvailablePoints() { return customerAvailablePoints; }
    public void setCustomerAvailablePoints(int points) { this.customerAvailablePoints = points; }
}
