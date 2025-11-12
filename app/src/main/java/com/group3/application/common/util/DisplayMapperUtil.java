package com.group3.application.common.util;

import android.content.Context;

import com.group3.application.R;

public class DisplayMapperUtil {

    private static final String STATUS_PAID = "PAID";
    private static final String DISPLAY_PAID = "Đã thanh toán";
    private static final String DISPLAY_UNPAID = "Chưa thanh toán";

    public static String mapPaymentMethod(Context context, String paymentMethodValue) {
        String[] values = context.getResources().getStringArray(R.array.payment_methods_values);
        String[] display = context.getResources().getStringArray(R.array.payment_methods_display);

        if (values.length != display.length) {
            return paymentMethodValue;
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(paymentMethodValue)) {
                return display[i];
            }
        }
        return paymentMethodValue;
    }

    public static String mapPaymentStatus(String paymentStatusValue) {
        if (paymentStatusValue != null && paymentStatusValue.equalsIgnoreCase(STATUS_PAID)) {
            return DISPLAY_PAID;
        }
        return DISPLAY_UNPAID;
    }
}
