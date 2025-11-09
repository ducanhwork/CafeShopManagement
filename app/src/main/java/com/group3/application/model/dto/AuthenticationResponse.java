package com.group3.application.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public class AuthenticationResponse {
    private Boolean isSuccess;
    private String  message;
    private String  errorCode;
    private String token;

    public AuthenticationResponse(Boolean isSuccess, String message, String errorCode, String token) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.errorCode = errorCode;
        this.token = token;
    }

    public AuthenticationResponse() {
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
