package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;
import com.group3.application.model.dto.OrderDetailItemDTO;

import java.util.List;

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
    private List<String> tableNames;

    // SỬA: Thêm trường tableIds
    @SerializedName("tableIds")
    private List<String> tableIds;

    @SerializedName("items")
    private List<OrderDetailItemDTO> items;

    // Getters and Setters
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

    // SỬA: Thêm getter và setter cho tableIds
    public List<String> getTableIds() {
        return tableIds;
    }

    public void setTableIds(List<String> tableIds) {
        this.tableIds = tableIds;
    }

    public List<OrderDetailItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderDetailItemDTO> items) {
        this.items = items;
    }
}
