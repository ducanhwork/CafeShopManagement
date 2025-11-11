package com.group3.application.model.dto;

import java.io.Serializable;

public class OrderItemDTO implements Serializable {
    public String productId;
    public String name;
    public double unitPrice;
    public int quantity;

    public double getSubtotal(){ return unitPrice * quantity; }

    public OrderItemDTO(String productId, String name, double unitPrice, int quantity) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
}
