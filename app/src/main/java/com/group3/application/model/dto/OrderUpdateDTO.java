package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
public class OrderUpdateDTO implements Serializable {

    @SerializedName("items")
    private List<OrderItemDTO> items;

    @SerializedName("status")
    private String status;

    @SerializedName("note")
    private String note;

    public OrderUpdateDTO(List<OrderItemDTO> items, String status, String note) {
        this.items = items;
        this.status = status;
        this.note = note;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}