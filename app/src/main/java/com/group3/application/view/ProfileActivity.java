package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group3.application.R;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.entity.User;
import com.group3.application.viewmodel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    private ProfileViewModel profileViewModel;

    private ConstraintLayout btnChangePassword;
    private ConstraintLayout btnLogout;
    private ConstraintLayout btnBack;
    private TextView tvUserName;
    private TextView tvEmail;
    private TextView tvPhoneNumber;
    private TextView tvRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        profileViewModel = new ProfileViewModel(getApplication());
        initView();
        profileViewModel.viewProfile();
        profileViewModel.viewProfileResult.observe(this, event -> {
            APIResult result = event.getContentIfNotHandled();
            if (result != null && result.isSuccess()) {
                User user = (User) result.getData();
                if (user != null) {
                    tvUserName.setText(user.getFullname());
                    tvEmail.setText(user.getEmail());
                    tvPhoneNumber.setText(user.getMobile());
                    tvRole.setText(user.getRole());
                }
            } else {
                Toast.makeText(this, "Load user information failed.", Toast.LENGTH_SHORT).show();
            }
        });
        btnLogout.setOnClickListener(v -> profileViewModel.logOut());
        profileViewModel.logOutResult.observe(this, event -> {
            APIResult result = event.getContentIfNotHandled();
            if (result != null && result.isSuccess()) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Log out failed.", Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(v -> finish());
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });

    }


    public void initView() {
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvRole = findViewById(R.id.tvRole);

    }
}