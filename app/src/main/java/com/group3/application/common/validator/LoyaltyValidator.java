package com.group3.application.common.validator;

import android.text.TextUtils;
import com.group3.application.model.bean.UpdateLoyaltyMemberRequest;
import java.util.regex.Pattern;

public class LoyaltyValidator {

    // Lấy regex từ code backend của bạn
    private static final Pattern PHONE_RE = Pattern.compile("^[0-9+][0-9]{7,14}$");
    private static final Pattern EMAIL_RE = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public static ValidationResult validate(UpdateLoyaltyMemberRequest req) {

        String name = req.getFullName();
        String phone = req.getPhone();
        String email = req.getEmail();

        // 1. Kiểm tra Tên
        if (TextUtils.isEmpty(name) || name.trim().isEmpty()) {
            return ValidationResult.failure("name", "Tên không được để trống");
        }

        // 2. Kiểm tra SĐT
        if (TextUtils.isEmpty(phone) || phone.trim().isEmpty()) {
            return ValidationResult.failure("phone", "SĐT không được để trống");
        }
        if (!PHONE_RE.matcher(phone).matches()) {
            return ValidationResult.failure("phone", "Định dạng SĐT không hợp lệ (8-15 số, bắt đầu bằng số hoặc +)");
        }

        // 3. Kiểm tra Email (không bắt buộc)
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_RE.matcher(email.trim()).matches()) {
                return ValidationResult.failure("email", "Định dạng email không hợp lệ");
            }
        }

        return ValidationResult.success();
    }
}
