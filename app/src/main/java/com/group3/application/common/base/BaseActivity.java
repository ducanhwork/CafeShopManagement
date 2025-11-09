package com.group3.application.common.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.group3.application.common.util.DialogUtils;
import com.group3.application.common.util.NetworkUtils;

/**
 * Base Activity for all activities
 * Provides common functionality like loading indicators, error handling
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNetworkOnCreate();
    }
    
    /**
     * Check network connectivity on activity creation
     */
    private void checkNetworkOnCreate() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showNetworkError();
        }
    }
    
    /**
     * Show loading dialog with message
     */
    protected void showLoading(String message) {
        hideLoading();
        progressDialog = DialogUtils.showProgressDialog(this, message);
    }
    
    /**
     * Show loading dialog with default message
     */
    protected void showLoading() {
        showLoading("Loading...");
    }
    
    /**
     * Hide loading dialog
     */
    protected void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    
    /**
     * Show ProgressBar in layout
     */
    protected void showProgressBar(ProgressBar progressBar) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Hide ProgressBar in layout
     */
    protected void hideProgressBar(ProgressBar progressBar) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
    
    /**
     * Show error dialog
     */
    protected void showError(String message) {
        hideLoading();
        DialogUtils.showErrorDialog(this, message);
    }
    
    /**
     * Show success dialog
     */
    protected void showSuccess(String message) {
        hideLoading();
        DialogUtils.showSuccessDialog(this, message);
    }
    
    /**
     * Show toast message
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show long toast message
     */
    protected void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    /**
     * Show network error dialog
     */
    protected void showNetworkError() {
        DialogUtils.showNetworkError(this);
    }
    
    /**
     * Show confirmation dialog
     */
    protected void showConfirmation(String title, String message, DialogUtils.OnConfirmListener listener) {
        DialogUtils.showConfirmationDialog(this, title, message, listener);
    }
    
    /**
     * Check if network is available
     */
    protected boolean isNetworkAvailable() {
        return NetworkUtils.isNetworkAvailable(this);
    }
    
    /**
     * Handle API error
     */
    protected void handleApiError(String error) {
        hideLoading();
        if (!isNetworkAvailable()) {
            showNetworkError();
        } else {
            showError(error != null ? error : "An error occurred. Please try again.");
        }
    }
    
    @Override
    protected void onDestroy() {
        hideLoading();
        super.onDestroy();
    }
}
