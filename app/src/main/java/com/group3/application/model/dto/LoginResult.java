package com.group3.application.model.dto;

public class LoginResult {
    private boolean isSuccess;
    private String message; // Thông báo thành công/thất bại cho người dùng
    private String authToken;
    private String userEmail; // Lấy từ email người dùng nhập vào

    // Constructor cho kết quả API Login thành công
    public LoginResult(boolean isSuccess, String message, String authToken, String userEmail) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.authToken = authToken;
        this.userEmail = userEmail;
    }
    // Constructor cho lỗi validation cục bộ hoặc lỗi API không có token (chỉ cần isSuccess và message)
    public LoginResult(boolean isSuccess, String errorMessage) {
        this(isSuccess, errorMessage, null, null);
    }

    // Getters
    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
