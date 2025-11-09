package com.group3.application.common.validator;

// Bạn có thể tạo một class Result để trả về kết quả
public class ValidationResult {
    public final boolean isValid;
    public final String errorField; // "code", "value", "end"
    public final String errorMessage;

    private ValidationResult(boolean isValid, String field, String message) {
        this.isValid = isValid;
        this.errorField = field;
        this.errorMessage = message;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, null, null);
    }

    public static ValidationResult failure(String field, String message) {
        return new ValidationResult(false, field, message);
    }
}
