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
    private MaterialCardView cardManageReports;
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
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivAvatar = findViewById(R.id.iv_avatar);
        cardManageProducts = findViewById(R.id.card_manage_products);
        cardManageReports = findViewById(R.id.card_manage_reports);
        cardManageVouchers = findViewById(R.id.card_manage_vouchers);
        cardManageReservations = findViewById(R.id.card_manage_reservation);
        cardManageStaffs = findViewById(R.id.card_manage_staffs);
        tvCurrentDate = findViewById(R.id.tv_current_date);
    }

    private void setCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd 'tháng' MM, yyyy HH:mm",
                new Locale("vi", "VN")
        );

        String formattedDateTime = currentDateTime.format(formatter);
        formattedDateTime = formattedDateTime.substring(0, 1).toUpperCase() + formattedDateTime.substring(1);

        tvCurrentDate.setText(formattedDateTime);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupClickListeners() {
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        cardManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        cardManageReports.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ReportManagementActivity.class);
            startActivity(intent);
        });

        cardManageVouchers.setOnClickListener(v -> {
             Intent intent = new Intent(AdminHomeActivity.this, HomeMenuActivity.class);
             startActivity(intent);
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
