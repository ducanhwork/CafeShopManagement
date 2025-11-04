package com.group3.application.model.entity;

import com.google.gson.annotations.SerializedName;

public class TableInfo {
    private String id;
    private String name;
    private String location;
    @SerializedName("seat_count") private Integer seatCount;
    private String status;
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Integer getSeatCount() { return seatCount; }
    public String getStatus() { return status; }
}
