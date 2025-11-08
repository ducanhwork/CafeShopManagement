
package com.group3.application.model.entity;

import java.io.Serializable;

public class Reservation implements Serializable {
    private String customerName;
    private String time;
    private String status;
    private String date;
    private int pax;
    private String tableNumber;

    public Reservation(String customerName, String time, String status, String date, int pax, String tableNumber) {
        this.customerName = customerName;
        this.time = time;
        this.status = status;
        this.date = date;
        this.pax = pax;
        this.tableNumber = tableNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public int getPax() {
        return pax;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
