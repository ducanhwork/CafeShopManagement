package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * DTO for quick update of table status only
 * Used with PATCH /tables/{id}/status endpoint
 */
public class UpdateTableStatusRequest {
    
    @SerializedName("status")
    private String status;  // Available, Occupied, or Reserved
    
    // Constructors
    public UpdateTableStatusRequest() {
    }
    
    public UpdateTableStatusRequest(String status) {
        this.status = status;
    }
    
    // Getters and Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
