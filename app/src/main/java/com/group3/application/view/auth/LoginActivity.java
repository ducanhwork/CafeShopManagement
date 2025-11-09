package com.group3.application.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.common.base.BaseActivity;
import com.group3.application.common.util.PreferenceManager;
import com.group3.application.common.util.ValidationUtils;
import com.group3.application.model.dto.AuthResponse;
import com.group3.application.viewmodel.auth.LoginViewModel;

/**
 * Login activity
 */
public class LoginActivity extends BaseActivity {
    
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView tvRegister, tvForgotPassword;
    
    private LoginViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        initViews();
        setupViewModel();
        setupListeners();
        observeViewModel();
    }
    
    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }
    
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }
    
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        
        tvForgotPassword.setOnClickListener(v -> {
            showToast("Forgot password feature coming soon");
        });
    }
    
    private void observeViewModel() {
        viewModel.getLoading().observe(this, loading -> {
            if (loading) {
                btnLogin.setEnabled(false);
                btnLogin.setText("");
                progressBar.setVisibility(View.VISIBLE);
            } else {
                btnLogin.setEnabled(true);
                btnLogin.setText(R.string.login);
                progressBar.setVisibility(View.GONE);
            }
        });
        
        viewModel.getLoginResult().observe(this, this::handleLoginSuccess);
        
        viewModel.getErrorMessage().observe(this, this::handleApiError);
    }
    
    private void attemptLogin() {
        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);
        
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        
        // Validate inputs
        boolean isValid = true;
        
        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError("Invalid email format");
            isValid = false;
        }
        
        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        // Check network
        if (!isNetworkAvailable()) {
            showNetworkError();
            return;
        }
        
        // Perform login
        viewModel.login(email, password);
    }
    
    private void handleLoginSuccess(AuthResponse response) {
        if (response == null || response.getToken() == null) {
            showError("Login failed. Please try again.");
            return;
        }
        
        // Save token and user info
        PreferenceManager.saveToken(this, response.getToken());
        
        if (response.getUser() != null) {
            PreferenceManager.saveUserRole(this, response.getUser().getRole());
            PreferenceManager.saveUserId(this, String.valueOf(response.getUser().getId()));
            PreferenceManager.saveUserName(this, response.getUser().getName());
            PreferenceManager.saveUserEmail(this, response.getUser().getEmail());
        }
        
        showSuccess("Login successful!");
        
        // Navigate to dashboard
        // TODO: Navigate to DashboardActivity once created
        // Intent intent = new Intent(this, DashboardActivity.class);
        // startActivity(intent);
        // finish();
        
        showToast("Dashboard coming soon. Login successful!");
    }
}
