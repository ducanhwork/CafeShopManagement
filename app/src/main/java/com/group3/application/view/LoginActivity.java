package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.common.enums.NavigationTarget;
import com.group3.application.model.dto.APIResult;
import com.group3.application.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilEmail, tilPassword;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initView();

        loginViewModel.loginResult.observe(this, event -> {
            APIResult result = event.getContentIfNotHandled();
            if (result != null) {
                if (result.isSuccess()) {
                    // Hiển thị thông báo thành công từ ViewModel
                    Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    // Hiển thị thông báo lỗi từ ViewModel
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + result.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        loginViewModel.emailError.observe(this, error -> {
            tilEmail.setError(error);
        });

        loginViewModel.passwordError.observe(this, error -> {
            tilPassword.setError(error);
        });

        loginViewModel.navigationEvent.observe(this, event -> {
            NavigationTarget target = event.getContentIfNotHandled();
            if (target != null) {
                Intent intent;
                switch (target) {
                    case PROFILE:
                        intent = new Intent(LoginActivity.this, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case FORGOT_PASSWORD:
                        intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        btnLogin.setOnClickListener(v -> {
            tilEmail.setError(null);
            tilPassword.setError(null);
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            loginViewModel.loginUser(email, password);
        });

        tvForgotPassword.setOnClickListener(v -> {
            loginViewModel.navigateToForgotPassword();
        });
    }

    public void initView() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginViewModel.checkInitialLoginState();
    }
}