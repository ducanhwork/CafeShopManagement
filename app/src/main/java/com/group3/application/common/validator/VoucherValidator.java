package com.group3.application.common.validator;
import android.text.TextUtils;
import com.group3.application.model.bean.VoucherRequest;
import java.math.BigDecimal;

public class VoucherValidator {
    public static ValidationResult validate(VoucherRequest r) {
        if (isEmpty(r.code)) return ValidationResult.failure("code", "Bắt buộc");
        if (isEmpty(r.code)) return ValidationResult.failure("code", "Mã không được để trống");
        r.code = r.code.trim().toUpperCase();
        if (!r.code.matches("^[A-Z0-9_-]{4,20}$")) {
            return ValidationResult.failure("code", "4–20 ký tự A–Z, 0–9, _ hoặc -");
        }
        if (isEmpty(r.type)) return ValidationResult.failure("type", "Bắt buộc");
        if (isEmpty(r.status)) return ValidationResult.failure("status", "Bắt buộc");

        if (r.value == null || Double.isNaN(r.value)) {
            return ValidationResult.failure("value", "Bắt buộc");
        }

        if (isEmpty(r.startDate)) return ValidationResult.failure("start", "Bắt buộc");
        if (isEmpty(r.endDate)) return ValidationResult.failure("end", "Bắt buộc");
        if (r.endDate.compareTo(r.startDate) < 0) {
            return ValidationResult.failure("end", "Kết thúc ≥ bắt đầu");
        }

        try {
            BigDecimal bd = BigDecimal.valueOf(r.value);
            if ("PERCENT".equals(r.type)) {
                if (bd.compareTo(BigDecimal.ZERO) <= 0 || bd.compareTo(new BigDecimal("100.00")) > 0) {
                    return ValidationResult.failure("value", "% phải trong (0,100]");
                }
            } else if ("FIXED_AMOUNT".equals(r.type)) {
                if (bd.compareTo(BigDecimal.ZERO) <= 0) {
                    return ValidationResult.failure("value", "Phải > 0");
                }
            }
        } catch (Exception e) {
            return ValidationResult.failure("value", "Giá trị không hợp lệ");
        }

        return ValidationResult.success();
    }

    private static boolean isEmpty(String s){ return TextUtils.isEmpty(s); }
}
