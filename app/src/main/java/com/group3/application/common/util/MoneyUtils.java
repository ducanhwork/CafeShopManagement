package com.group3.application.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public final class MoneyUtils {
    private MoneyUtils(){}

    public static String format(BigDecimal amount) {
        if (amount == null) return "-";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(0);
        String formatted = nf.format(amount);
        return formatted + " đ";
    }

    public static String formatWithScale(BigDecimal amount, int scale) {
        if (amount == null) return "-";
        BigDecimal scaled = amount.setScale(scale, java.math.RoundingMode.HALF_UP);
        return format(scaled);
    }
}
