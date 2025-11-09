package com.group3.application.model.bean;

import java.util.UUID;

public class VoucherResponse implements java.io.Serializable{
    public UUID id;
    public String code;
    public String type;
    public Double value;
    public String startDate;
    public String endDate;
    public String status;
}
