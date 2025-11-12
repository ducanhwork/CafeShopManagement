package com.group3.application.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BillSummaryDTO {
    private UUID billId;
    private LocalDateTime issuedTime;
    private BigDecimal finalAmount;
    private String paymentStatus;
    private String cashierName;
    private String paymentMethod;

    public UUID getBillId() { return billId; }
    public void setBillId(UUID billId) { this.billId = billId; }

    public LocalDateTime getIssuedTime() { return issuedTime; }
    public void setIssuedTime(LocalDateTime issuedTime) { this.issuedTime = issuedTime; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
