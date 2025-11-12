package com.group3.application.model.dto;

public class PaymentConfirmationRequest {
    private String paymentMethod;

    public PaymentConfirmationRequest(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
