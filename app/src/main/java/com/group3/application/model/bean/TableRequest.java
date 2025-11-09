package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;

public class TableRequest {
    
    @SerializedName("tableNumber")
    private int tableNumber;
    
    @SerializedName("capacity")
    private int capacity;
    
    @SerializedName("status")
    private String status;
    
    public TableRequest() {
    }
    
    public TableRequest(int tableNumber, int capacity, String status) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
    }
    
    // Getters and Setters
    public int getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
