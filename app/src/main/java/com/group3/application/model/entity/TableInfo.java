package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Table entity representing a cafe table
 * Matches backend API structure exactly
 */
public class TableInfo {
    
    // Status constants - MUST match backend exactly (case-sensitive)
    public static final String STATUS_AVAILABLE = "Available";
    public static final String STATUS_OCCUPIED = "Occupied";
    public static final String STATUS_RESERVED = "Reserved";
    
    // Location constants - MUST match backend exactly (case-sensitive)
    public static final String LOCATION_INDOOR = "Indoor";
    public static final String LOCATION_OUTDOOR = "Outdoor";
    public static final String LOCATION_BALCONY = "Balcony";
    
    @SerializedName("id")
    private String id;  // UUID from backend
    
    @SerializedName("name")
    private String name;  // Table name (e.g., "Table A1")
    
    @SerializedName("location")
    private String location;        // Indoor, Outdoor, Balcony
    
    @SerializedName("seatCount")
    private Integer seatCount;  // Number of seats (1-20)
    
    @SerializedName("status")
    private String status;  // Available, Occupied, or Reserved
    
    @SerializedName("isAvailable")
    private Boolean isAvailable;  // Computed field from backend
    
    @SerializedName("currentOrders")
    private Integer currentOrders;  // Number of active orders
    
    // Constructors
    public TableInfo() {
    }
    
    public TableInfo(String name, String location, Integer seatCount, String status) {
        this.name = name;
        this.location = location;
        this.seatCount = seatCount;
        this.status = status;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public Integer getCurrentOrders() {
        return currentOrders;
    }
    
    public void setCurrentOrders(Integer currentOrders) {
        this.currentOrders = currentOrders;
    }
    
    // Helper methods
    public boolean isAvailable() {
        return isAvailable != null ? isAvailable : STATUS_AVAILABLE.equals(status);
    }
    
    public boolean isOccupied() {
        return STATUS_OCCUPIED.equals(status);
    }
    
    public boolean isReserved() {
        return STATUS_RESERVED.equals(status);
    }
    
    /**
     * Get status color resource name for UI
     */
    public String getStatusColorName() {
        if (status == null) return "status_unknown";
        switch (status) {
            case STATUS_AVAILABLE:
                return "status_available";  // Green
            case STATUS_OCCUPIED:
                return "status_occupied";    // Red
            case STATUS_RESERVED:
                return "status_reserved";    // Orange
            default:
                return "status_unknown";
        }
    }
    
    /**
     * Get location icon resource name
     */
    public String getLocationIcon() {
        if (location == null) return "ic_location";
        switch (location) {
            case LOCATION_INDOOR:
                return "ic_indoor";
            case LOCATION_OUTDOOR:
                return "ic_outdoor";
            case LOCATION_BALCONY:
                return "ic_balcony";
            default:
                return "ic_location";
        }
    }
    
    /**
     * Validate if this table entity has all required fields
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty()
                && location != null && isValidLocation(location)
                && seatCount != null && seatCount >= 1 && seatCount <= 20
                && status != null && isValidStatus(status);
    }
    
    /**
     * Check if location is valid
     */
    public static boolean isValidLocation(String location) {
        return LOCATION_INDOOR.equals(location) 
                || LOCATION_OUTDOOR.equals(location) 
                || LOCATION_BALCONY.equals(location);
    }
    
    /**
     * Check if status is valid
     */
    public static boolean isValidStatus(String status) {
        return STATUS_AVAILABLE.equals(status) 
                || STATUS_OCCUPIED.equals(status) 
                || STATUS_RESERVED.equals(status);
    }
    
    @Override
    public String toString() {
        return name != null ? name : "Unknown Table";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableInfo tableInfo = (TableInfo) o;
        return id != null && id.equals(tableInfo.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
