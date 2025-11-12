package com.group3.application.model.dto;

public class CreateTableRequest {
    private String name;
    private String location; // Indoor, Outdoor, Balcony
    private Integer seatCount;
    private String status; // Available, Occupied, Reserved

    public CreateTableRequest() {
    }

    public CreateTableRequest(String name, String location, Integer seatCount, String status) {
        this.name = name;
        this.location = location;
        this.seatCount = seatCount;
        this.status = status;
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
}
