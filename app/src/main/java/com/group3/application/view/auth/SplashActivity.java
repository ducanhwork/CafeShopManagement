package com.group3.application.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.group3.application.R;
import com.group3.application.common.util.JWTUtils;
import com.group3.application.common.util.PreferenceManager;

/**
 * Splash screen activity
 * Checks authentication status and navigates to appropriate screen
 */
public class SplashActivity extends AppCompatActivity {
    
    private static final long SPLASH_DELAY = 2000; // 2 seconds
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // Check authentication after delay
        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthentication, SPLASH_DELAY);
    }
    
    /**
     * Check if user is authenticated and navigate accordingly
     */
    private void checkAuthentication() {
        String token = PreferenceManager.getToken(this);
        
        if (token != null && !JWTUtils.isTokenExpired(token)) {
            // Token is valid, navigate to dashboard
            navigateToDashboard();
        } else {
            // Token is invalid or missing, navigate to login
            if (token != null) {
                // Clear expired session
                PreferenceManager.clearSession(this);
            }
            navigateToLogin();
        }
    }
    
    /**
     * Navigate to dashboard
     */
    private void navigateToDashboard() {
        // TODO: Navigate to DashboardActivity once created
        // Intent intent = new Intent(this, DashboardActivity.class);
        // startActivity(intent);
        // finish();
        
        // Temporary: Navigate to login
        navigateToLogin();
    }
    
    /**
     * Navigate to login
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
