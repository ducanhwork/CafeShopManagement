package com.group3.application.common.util;

import android.graphics.Color;

/**
 * Utility class for getting colors based on status
 */
public class StatusColorUtil {
    
    /**
     * Get color for table status
     */
    public static int getColorForStatus(String status) {
        if (status == null) {
            return Color.GRAY;
        }
        
        switch (status.toUpperCase()) {
            case "AVAILABLE":
                return Color.parseColor("#4CAF50"); // Green
            case "OCCUPIED":
                return Color.parseColor("#F44336"); // Red
            case "RESERVED":
                return Color.parseColor("#FF9800"); // Orange
            case "MAINTENANCE":
                return Color.parseColor("#9E9E9E"); // Gray
            default:
                return Color.GRAY;
        }
    }
    
    /**
     * Get color resource ID for table status
     */
    public static String getStatusColorName(String status) {
        if (status == null) {
            return "colorSurface";
        }
        
        switch (status.toUpperCase()) {
            case "AVAILABLE":
                return "statusAvailable";
            case "OCCUPIED":
                return "statusOccupied";
            case "RESERVED":
                return "statusReserved";
            case "MAINTENANCE":
                return "statusMaintenance";
            default:
                return "colorSurface";
        }
    }
    
    /**
     * Get text color based on background color for better contrast
     */
    public static int getTextColorForBackground(int backgroundColor) {
        // Calculate luminance
        int red = Color.red(backgroundColor);
        int green = Color.green(backgroundColor);
        int blue = Color.blue(backgroundColor);
        
        // Calculate relative luminance
        double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255;
        
        // Return white text for dark backgrounds, black for light backgrounds
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
}
