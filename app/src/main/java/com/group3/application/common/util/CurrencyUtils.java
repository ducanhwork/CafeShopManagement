package com.group3.application.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    
    private static final Locale LOCALE_VN = new Locale("vi", "VN");
    
    public static String format(BigDecimal amount) {
        if (amount == null) {
            return "0 ₫";
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(LOCALE_VN);
        return formatter.format(amount);
    }
    
    public static String format(double amount) {
        return format(BigDecimal.valueOf(amount));
    }
    
    public static String format(long amount) {
        return format(BigDecimal.valueOf(amount));
    }
    
    public static BigDecimal parse(String amountStr) {
        try {
            String cleanStr = amountStr.replaceAll("[^0-9.]", "");
            return new BigDecimal(cleanStr);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
