package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.group3.application.R;
import com.group3.application.model.repository.AuthRepository;
import com.group3.application.view.fragment.InventoryFragment;
import com.group3.application.view.fragment.ReportsFragment;
import com.group3.application.view.fragment.ShiftFragment;
import com.group3.application.view.fragment.TablesFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminHomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleImageView ivAvatar;
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
        // Click on avatar to go to Profile or show logout dialog
        ivAvatar.setOnClickListener(v -> showProfileMenu());
    }

    private void showProfileMenu() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Profile")
                .setItems(new String[]{"View Profile", "Logout"}, (dialog, which) -> {
                    if (which == 0) {
                        // Navigate to profile
                        Intent intent = new Intent(AdminHomeActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    } else {
                        // Logout
                        showLogoutConfirmation();
                    }
                })
                .show();
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.confirm_logout)
                .setPositiveButton(R.string.yes, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void performLogout() {
        // Clear auth token
        authRepository.clearAuthToken();

        // Navigate to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Show logout confirmation on back press
        showLogoutConfirmation();
    }
}



