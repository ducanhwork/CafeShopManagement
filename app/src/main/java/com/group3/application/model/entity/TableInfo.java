package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TableInfo implements Serializable {
    private String id;
    private String name;
    private String location;
    @SerializedName("seatCount") private Integer seatCount;
    private String status;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Integer getSeatCount() { return seatCount; }
    public void setSeatCount(Integer seatCount) { this.seatCount = seatCount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Convenience method
    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(status) || "EMPTY".equalsIgnoreCase(status);
    }
}
