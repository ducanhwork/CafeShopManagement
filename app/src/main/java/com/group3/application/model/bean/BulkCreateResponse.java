package com.group3.application.model.bean;

import com.google.gson.annotations.SerializedName;
import com.group3.application.model.entity.TableInfo;

import java.util.List;

/**
 * Response DTO for bulk table creation
 * Returned from POST /tables/bulk endpoint
 */
public class BulkCreateResponse {
    
    @SerializedName("message")
    private String message;  // Success message (e.g., "Successfully created 2 tables")
    
    @SerializedName("tables")
    private List<TableInfo> tables;  // List of created tables
    
    // Constructors
    public BulkCreateResponse() {
    }
    
    public BulkCreateResponse(String message, List<TableInfo> tables) {
        this.message = message;
        this.tables = tables;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<TableInfo> getTables() {
        return tables;
    }
    
    public void setTables(List<TableInfo> tables) {
        this.tables = tables;
    }
}
