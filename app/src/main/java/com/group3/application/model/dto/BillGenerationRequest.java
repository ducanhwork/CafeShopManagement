package com.group3.application.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class BillGenerationRequest {
    private String orderId;
    private String customerPhone;
    private String voucherCode;
    private int pointsToRedeem;

    public BillGenerationRequest() {}

    public BillGenerationRequest(String orderId, String customerPhone, String voucherCode, int pointsToRedeem) {
        this.orderId = orderId;
        this.customerPhone = customerPhone;
        this.voucherCode = voucherCode;
        this.pointsToRedeem = pointsToRedeem;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }

    public int getPointsToRedeem() { return pointsToRedeem; }
    public void setPointsToRedeem(int pointsToRedeem) { this.pointsToRedeem = pointsToRedeem; }
}
