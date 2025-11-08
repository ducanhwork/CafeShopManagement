package com.group3.application.model.dto;

public class APIResult<T> {
    private boolean isSuccess;
    private String message; // Thông báo thành công/thất bại cho người dùng
    private T data;


    public APIResult(boolean isSuccess, String message, T data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public APIResult(boolean isSuccess, String errorMessage) {
        this(isSuccess, errorMessage, null);
    }

    // Getters
    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
