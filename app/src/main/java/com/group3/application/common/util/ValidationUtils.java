package com.group3.application.common.util;

import android.util.Patterns;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static String getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "Weak";
        }
        
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[@#$%^&+=].*")) strength++;
        
        if (strength <= 2) return "Weak";
        if (strength == 3) return "Medium";
        return "Strong";
    }
    
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }
    
    public static boolean isValidLength(String text, int minLength) {
        return text != null && text.length() >= minLength;
    }
}
