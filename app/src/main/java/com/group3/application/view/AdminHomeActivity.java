package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.group3.application.R;
import com.group3.application.model.repository.AuthRepository;
import com.group3.application.view.fragment.InventoryFragment;
import com.group3.application.view.fragment.ReportsFragment;
import com.group3.application.view.fragment.ShiftFragment;
import com.group3.application.view.fragment.TablesFragment;

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
    private TextView tvCurrentDate;
    private BottomNavigationView bottomNavigation;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        
        authRepository = new AuthRepository(getApplication());
        
        initViews();
        setupToolbar();
        setupBottomNavigation();
        setupClickListeners();
        
        // Load default fragment (Shift)
        if (savedInstanceState == null) {
            loadFragment(new ShiftFragment());
            bottomNavigation.setSelectedItemId(R.id.navigation_shift);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivAvatar = findViewById(R.id.iv_avatar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        cardManageProducts = findViewById(R.id.card_manage_products);
        cardManageOrders = findViewById(R.id.card_manage_orders);
        cardManageVouchers = findViewById(R.id.card_manage_vouchers);
        tvCurrentDate = findViewById(R.id.tv_current_date);
    }

    private void setCurrentDateTime() {
        // Sử dụng API java.time hiện đại, yêu cầu API level 26+ (đã có trong project của bạn)
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

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_shift) {
                selectedFragment = new ShiftFragment();
            } else if (itemId == R.id.navigation_tables) {
                selectedFragment = new TablesFragment();
            } else if (itemId == R.id.navigation_inventory) {
                selectedFragment = new InventoryFragment();
            } else if (itemId == R.id.navigation_reports) {
                selectedFragment = new ReportsFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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
    }
    
    /**
     * Switch to Shift tab programmatically
     * Can be called from fragments when user needs to start a shift
     */
    public void switchToShiftTab() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.navigation_shift);
        }
    }
}



