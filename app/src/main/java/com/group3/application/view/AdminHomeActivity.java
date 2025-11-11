package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.group3.application.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminHomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleImageView ivAvatar;
    private MaterialCardView cardManageProducts;
    private MaterialCardView cardManageOrders;
    private MaterialCardView cardManageVouchers;
    private MaterialCardView cardManageReservations;
    private MaterialCardView cardManageStaffs;
    private TextView tvCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupToolbar();
        setupClickListeners();
        setCurrentDateTime();
        MaterialCardView cardOrders = findViewById(R.id.card_manage_orders);
        cardOrders.setOnClickListener(v -> {
            // Mở Activity hoặc Fragment danh sách Order của bạn
            Intent intent = new Intent(this, OrderListActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivAvatar = findViewById(R.id.iv_avatar);
        cardManageProducts = findViewById(R.id.card_manage_products);
        cardManageOrders = findViewById(R.id.card_manage_orders);
        cardManageVouchers = findViewById(R.id.card_manage_vouchers);
        cardManageReservations = findViewById(R.id.card_manage_reservation);
        cardManageStaffs = findViewById(R.id.card_manage_staffs);
        tvCurrentDate = findViewById(R.id.tv_current_date);
    }

    private void setCurrentDateTime() {
        // Sử dụng API java.time hiện đại, yêu cầu API level 26+ (đã có trong project của bạn)
        LocalDateTime currentDateTime = LocalDateTime.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd 'tháng' MM, yyyy HH:mm",
                new Locale("us", "US")
        );

        String formattedDateTime = currentDateTime.format(formatter);

        // Viết hoa chữ cái đầu tiên của ngày trong tuần (ví dụ: "thứ tư" -> "Thứ tư")
        formattedDateTime = formattedDateTime.substring(0, 1).toUpperCase() + formattedDateTime.substring(1);

        tvCurrentDate.setText(formattedDateTime);
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        // Bỏ tiêu đề mặc định vì chúng ta đã có tiêu đề tùy chỉnh
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupClickListeners() {
        // Sự kiện click vào Avatar trên Toolbar
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Sự kiện click vào Card Quản lý Sản phẩm
        cardManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        // Sự kiện click vào Card Quản lý Đơn hàng
        cardManageOrders.setOnClickListener(v -> {
            Toast.makeText(this, "Navigate to Order Management Screen", Toast.LENGTH_SHORT).show();
        });

        cardManageVouchers.setOnClickListener(v -> {
            // TODO: Tạo VoucherManagementActivity (layout thứ 2 bạn gửi) và điều hướng

             Intent intent = new Intent(AdminHomeActivity.this, HomeMenuActivity.class);
             startActivity(intent);

            // Tạm thời hiển thị Toast
            Toast.makeText(this, "Navigate to Voucher Management Screen", Toast.LENGTH_SHORT).show();
        });

        cardManageReservations.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ReservationActivity.class);
            startActivity(intent);
        });

        cardManageStaffs.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, StaffListActivity.class);
            startActivity(intent);
        });
    }


}



