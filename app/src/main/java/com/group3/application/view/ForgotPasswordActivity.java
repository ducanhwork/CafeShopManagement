package com.group3.application.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.group3.application.model.dto.APIResult;
import com.group3.application.viewmodel.ForgotPasswordViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {
    public static final String TAG = "ForgotPasswordActivity";

    private ForgotPasswordViewModel forgotPasswordViewModel;
    private TextView tvSuccessMessage;
    private TextView tvInstructions;
    private TextView tvBackToLogin;
    private TextInputLayout tilEmail;
    private EditText editEmail;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        forgotPasswordViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        initView();

        forgotPasswordViewModel.resetPasswordResult.observe(this, event -> {
            APIResult result = event.getContentIfNotHandled();
            if (result != null) {
                if (result.isSuccess()) {
                    // Xử lý thành công
                    hideInstructionAndSuccessMessage();
                } else {
                    // Xử lý lỗi
                    Log.e(TAG, "Line 56"+result.getMessage());
                    Toast.makeText(ForgotPasswordActivity.this, "Line 57: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        forgotPasswordViewModel.emailError.observe(this, error -> {
            tilEmail.setError(error);
            tilEmail.requestFocus();
        });
        forgotPasswordViewModel.resetPasswordError.observe(this, error -> {
            Log.e(TAG, "Line 66"+error);
            Toast.makeText(ForgotPasswordActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
        });
        btnResetPassword.setOnClickListener(v -> {
            tilEmail.setError(null);
            String email = editEmail.getText().toString().trim();
            forgotPasswordViewModel.forgotPassword(email);
        });
        tvBackToLogin.setOnClickListener(v -> {
            finish();
        });
    }

    public void initView() {
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        tilEmail = findViewById(R.id.tilEmail);
        editEmail = findViewById(R.id.editEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
    }

    public void hideInstructionAndSuccessMessage() {
        tvInstructions.setVisibility(View.GONE);
        tilEmail.setVisibility(View.GONE);
        btnResetPassword.setVisibility(View.GONE);
        tvBackToLogin.setVisibility(View.VISIBLE);
        tvSuccessMessage.setVisibility(View.VISIBLE);

    }
}