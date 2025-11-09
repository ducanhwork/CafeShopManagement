package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * DTO for creating a new table
 * Matches backend API request structure
 */
public class CreateTableRequest {
    
    @SerializedName("name")
    private String name;  // Required, 1-50 characters
    
    @SerializedName("location")
    private String location;  // Required: Indoor, Outdoor, Balcony
    
    @SerializedName("seatCount")
    private Integer seatCount;  // Required: 1-20
    
    @SerializedName("status")
    private String status;  // Optional: Available/Occupied/Reserved (default: Available)
    
    // Constructors
    public CreateTableRequest() {
    }
    
    public CreateTableRequest(String name, String location, Integer seatCount) {
        this.name = name;
        this.location = location;
        this.seatCount = seatCount;
        this.status = "Available";  // Default
    }
    
    public CreateTableRequest(String name, String location, Integer seatCount, String status) {
        this.name = name;
        this.location = location;
        this.seatCount = seatCount;
        this.status = status;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getSeatCount() {
        return seatCount;
    }
    
    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
