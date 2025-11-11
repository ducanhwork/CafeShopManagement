package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;
import com.group3.application.model.dto.OrderDetailItemDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Order {

    @SerializedName("id")
    private String id;

    @SerializedName("orderDate")
    private String orderDate;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("status")
    private String status;

    @SerializedName("staffName")
    private String staffName;

    @SerializedName("tableNames")
    private List<String> tableNames = new ArrayList<>();

    @SerializedName("tables")
    private List<TableInfo> tables = new ArrayList<>();

    @SerializedName("items")
    private List<OrderDetailItemDTO> items = new ArrayList<>();

    @SerializedName("note")
    private String note;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    public List<TableInfo> getTables() {
        return tables;
    }

    public void setTables(List<TableInfo> tables) {
        this.tables = tables;
    }

    public List<String> getTableIds() {
        if (tables == null) {
            return new ArrayList<>();
        }
        return tables.stream()
                .map(TableInfo::getId)
                .collect(Collectors.toList());
    }

    public List<OrderDetailItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderDetailItemDTO> items) {
        this.items = items;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
