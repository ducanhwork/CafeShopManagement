package com.group3.application.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentConfirmationResponse {
    private String billId;
    private String paymentStatus;
    private OffsetDateTime paymentTime;
    private BigDecimal amountPaid;
    private String method;

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public OffsetDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(OffsetDateTime paymentTime) { this.paymentTime = paymentTime; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}
