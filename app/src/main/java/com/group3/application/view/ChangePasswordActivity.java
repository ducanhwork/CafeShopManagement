package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.model.dto.APIResult;
import com.group3.application.viewmodel.ChangePasswordViewModel;
import com.group3.application.common.constant.IConstants;

public class ChangePasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnUpdatePassword;
    private ChangePasswordViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupToolbar();
        viewModel = new ChangePasswordViewModel(getApplication());
        btnUpdatePassword.setOnClickListener(v -> {
            String crrPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmNewPassword = etConfirmPassword.getText().toString().trim();
            viewModel.changePassword(crrPassword, newPassword, confirmNewPassword);
        });
        viewModel.crrPasswordError.observe(this, error -> {
            tilCurrentPassword.setError(error);
        });
        viewModel.newPasswordError.observe(this, error -> {
            tilNewPassword.setError(error);
        });
        viewModel.confirmNewPasswordError.observe(this, error -> {
            tilConfirmPassword.setError(error);
        });
        viewModel.resultChangePassWord.observe(this, event -> {
            APIResult result = event.getContentIfNotHandled();
            switch (result.getMessage()) {
                case IConstants.CHANGED_PASSWORD_SUCCESS:
                    Toast.makeText(this, IConstants.CHANGED_PASSWORD_SUCCESS, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case IConstants.CHANGED_PASSWORD_FAILED:
                    Toast.makeText(this, IConstants.CHANGED_PASSWORD_FAILED, Toast.LENGTH_SHORT).show();
                    break;
                case IConstants.EMAIL_NOT_IN_PREFS:
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    break;

            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilCurrentPassword = findViewById(R.id.til_current_password);
        tilNewPassword = findViewById(R.id.til_new_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnUpdatePassword = findViewById(R.id.btn_update_password);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}