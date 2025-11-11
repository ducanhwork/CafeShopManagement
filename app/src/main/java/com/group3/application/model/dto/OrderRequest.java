package com.group3.application.model.dto;

import java.util.List;

public class OrderRequest {
    private List<String> tableIds;
    private List<OrderItemDTO> items;
    private String note;

    public OrderRequest(List<String> tableIds, List<OrderItemDTO> items, String note) {
        this.tableIds = tableIds;
        this.items = items;
        this.note = note;
    }

    public List<String> getTableIds() {
        return tableIds;
    }

    public void setTableIds(List<String> tableIds) {
        this.tableIds = tableIds;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
