package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
            Intent intent = new Intent(this, OrderListActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivAvatar = findViewById(R.id.iv_avatar);
        cardManageProducts = findViewById(R.id.card_manage_products);
        cardManageOrders = findViewById(R.id.card_manage_orders);
        tvCurrentDate = findViewById(R.id.tv_current_date);
    }

    private void setCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd 'tháng' MM, yyyy HH:mm",
                Locale.US
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
            Toast.makeText(this, "Navigate to Profile Screen", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        cardManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        cardManageOrders.setOnClickListener(v -> {
            // TODO: Tạo OrderManagementActivity và điều hướng đến đó
            Toast.makeText(this, "Navigate to Order Management Screen", Toast.LENGTH_SHORT).show();
        });
    }


}



