package com.group3.application.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillPaymentDTO {
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paidAt;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
