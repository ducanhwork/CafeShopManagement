
package com.group3.application.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    private UUID id;
    private String customerName;
    private String customerPhone;
    private LocalDateTime reservationTime;
    private Integer numGuests;
    private String status;
    private LocalDateTime createdAt;
    private UUID tableId;
    private UUID userId;

    public Reservation(UUID id, String customerName, String customerPhone, LocalDateTime reservationTime, Integer numGuests, String status, LocalDateTime createdAt, UUID tableId, UUID userId) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.reservationTime = reservationTime;
        this.numGuests = numGuests;
        this.status = status;
        this.createdAt = createdAt;
        this.tableId = tableId;
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Integer getNumGuests() {
        return numGuests;
    }

    public void setNumGuests(Integer numGuests) {
        this.numGuests = numGuests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getTableId() {
        return tableId;
    }

    public void setTableId(UUID tableId) {
        this.tableId = tableId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
