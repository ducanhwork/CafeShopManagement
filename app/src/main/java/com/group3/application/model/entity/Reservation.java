
package com.group3.application.model.entity;

import java.util.UUID;

public class Reservation {
    private UUID id;
    private String customerName;
    private String customerPhone;
    private String reservationTime;
    private String numGuests;
    private String status;
    private String tableName;
    private String userName;

    public Reservation(UUID id, String customerName, String customerPhone, String reservationTime, String numGuests, String status, String tableName, String userName) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.reservationTime = reservationTime;
        this.numGuests = numGuests;
        this.status = status;
        this.tableName = tableName;
        this.userName = userName;
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

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getNumGuests() {
        return numGuests;
    }

    public void setNumGuests(String numGuests) {
        this.numGuests = numGuests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
