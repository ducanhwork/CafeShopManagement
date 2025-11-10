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

public class WaiterHomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleImageView ivAvatar;
    private TextView tvCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waiter_home);
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
        tvCurrentDate = findViewById(R.id.tv_current_date);
    }

    private void setCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd 'tháng' MM, yyyy HH:mm",
                new Locale("vi", "VN")
        );

        String formattedDateTime = currentDateTime.format(formatter);

        // Viết hoa chữ cái đầu tiên của ngày trong tuần (ví dụ: "thứ tư" -> "Thứ tư")
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
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_manage_reservation).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservationListActivity.class);
            startActivity(intent);
        });
    }
}



