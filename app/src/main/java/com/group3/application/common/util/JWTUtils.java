package com.group3.application.common.util;

import android.util.Base64;
import org.json.JSONObject;

public class JWTUtils {
    
    public static boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return true;
            
            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP));
            JSONObject json = new JSONObject(payload);
            
            if (json.has("exp")) {
                long exp = json.getLong("exp");
                long currentTime = System.currentTimeMillis() / 1000;
                return currentTime > exp;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    
    public static String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            
            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP));
            JSONObject json = new JSONObject(payload);
            
            if (json.has("sub")) {
                return json.getString("sub");
            }
            if (json.has("userId")) {
                return json.getString("userId");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            
            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP));
            JSONObject json = new JSONObject(payload);
            
            if (json.has("role")) {
                return json.getString("role");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
