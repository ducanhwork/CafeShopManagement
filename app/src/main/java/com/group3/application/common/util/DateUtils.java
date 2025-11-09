package com.group3.application.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    
    private static final String DEFAULT_FORMAT = "dd/MM/yyyy HH:mm";
    private static final String DATE_ONLY = "dd/MM/yyyy";
    private static final String TIME_ONLY = "HH:mm";
    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    public static String format(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }
    
    public static String format(long timestamp) {
        return format(new Date(timestamp));
    }
    
    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_ONLY, Locale.getDefault());
        return sdf.format(date);
    }
    
    public static String formatTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_ONLY, Locale.getDefault());
        return sdf.format(date);
    }
    
    public static String formatISO(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }
    
    public static Date parse(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Date parseISO(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static long getDifferenceInMinutes(Date start, Date end) {
        if (start == null || end == null) return 0;
        long diffMs = end.getTime() - start.getTime();
        return diffMs / (60 * 1000);
    }
    
    public static long getDifferenceInHours(Date start, Date end) {
        return getDifferenceInMinutes(start, end) / 60;
    }
}
