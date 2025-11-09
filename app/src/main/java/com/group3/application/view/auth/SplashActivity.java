package com.group3.application.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.group3.application.R;
import com.group3.application.model.repository.AuthRepository;
import com.group3.application.view.AdminHomeActivity;
import com.group3.application.view.LoginActivity;

/**
 * Splash screen activity
 * Checks authentication status and navigates to appropriate screen
 */
public class SplashActivity extends AppCompatActivity {
    
    private static final long SPLASH_DELAY = 2000; // 2 seconds
    private AuthRepository authRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        authRepository = new AuthRepository(getApplicationContext());
        
        // Check authentication after delay
        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthentication, SPLASH_DELAY);
    }
    
    /**
     * Check if user is authenticated and navigate accordingly
     */
    private void checkAuthentication() {
        if (authRepository.isUserLoggedIn()) {
            // User is logged in, navigate to admin home
            navigateToDashboard();
        } else {
            // User is not logged in, navigate to login
            navigateToLogin();
        }
    }
    
    /**
     * Navigate to dashboard
     */
    private void navigateToDashboard() {
        Intent intent = new Intent(this, AdminHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * Navigate to login
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

