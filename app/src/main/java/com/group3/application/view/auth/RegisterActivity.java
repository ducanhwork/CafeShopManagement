package com.group3.application.view.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.common.base.BaseActivity;
import com.group3.application.common.util.PreferenceManager;
import com.group3.application.common.util.ValidationUtils;
import com.group3.application.model.dto.AuthResponse;
import com.group3.application.viewmodel.auth.RegisterViewModel;

public class RegisterActivity extends BaseActivity {
    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etFullName, etEmail, etPassword, etConfirm;
    private Spinner spinnerRole;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;

    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupViewModel();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirm = findViewById(R.id.tilConfirmPassword);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);

        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Setup role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this, loading -> {
            if (loading) {
                btnRegister.setEnabled(false);
                btnRegister.setText("");
                progressBar.setVisibility(View.VISIBLE);
            } else {
                btnRegister.setEnabled(true);
                btnRegister.setText(R.string.register);
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getRegisterResult().observe(this, this::handleRegisterSuccess);
        viewModel.getErrorMessage().observe(this, this::handleApiError);
    }

    private void attemptRegister() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirm.setError(null);

        String name = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirm = etConfirm.getText() != null ? etConfirm.getText().toString() : "";
        String role = spinnerRole.getSelectedItem() != null ? spinnerRole.getSelectedItem().toString() : "STAFF";

        boolean valid = true;
        if (name.isEmpty()) { tilFullName.setError("Name is required"); valid = false; }
        if (email.isEmpty()) { tilEmail.setError("Email is required"); valid = false; }
        else if (!ValidationUtils.isValidEmail(email)) { tilEmail.setError("Invalid email"); valid = false; }
        if (password.isEmpty()) { tilPassword.setError("Password is required"); valid = false; }
        else if (!ValidationUtils.isValidPassword(password)) { tilPassword.setError("Password must be at least 8 chars, include uppercase and number"); valid = false; }
        if (!password.equals(confirm)) { tilConfirm.setError("Passwords do not match"); valid = false; }

        if (!valid) return;

        if (!isNetworkAvailable()) { showNetworkError(); return; }

        viewModel.register(name, email, password, role);
    }

    private void handleRegisterSuccess(AuthResponse response) {
        if (response == null || response.getToken() == null) {
            showError("Registration failed. Please try again.");
            return;
        }

        PreferenceManager.saveToken(this, response.getToken());
        if (response.getUser() != null) {
            PreferenceManager.saveUserRole(this, response.getUser().getRole());
            PreferenceManager.saveUserId(this, String.valueOf(response.getUser().getId()));
            PreferenceManager.saveUserName(this, response.getUser().getName());
            PreferenceManager.saveUserEmail(this, response.getUser().getEmail());
        }

        showSuccess("Registration successful!");
        // TODO: Navigate to Dashboard when implemented
        finish();
    }
}
