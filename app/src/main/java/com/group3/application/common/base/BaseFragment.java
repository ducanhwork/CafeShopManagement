package com.group3.application.common.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.group3.application.common.util.DialogUtils;
import com.group3.application.common.util.NetworkUtils;

/**
 * Base Fragment for all fragments
 * Provides common functionality like loading indicators, error handling
 */
public abstract class BaseFragment extends Fragment {
    
    private ProgressDialog progressDialog;
    
    /**
     * Show loading dialog with message
     */
    protected void showLoading(String message) {
        hideLoading();
        Context context = getContext();
        if (context != null) {
            progressDialog = DialogUtils.showProgressDialog(context, message);
        }
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
        Context context = getContext();
        if (context != null) {
            DialogUtils.showErrorDialog(context, message);
        }
    }
    
    /**
     * Show success dialog
     */
    protected void showSuccess(String message) {
        hideLoading();
        Context context = getContext();
        if (context != null) {
            DialogUtils.showSuccessDialog(context, message);
        }
    }
    
    /**
     * Show toast message
     */
    protected void showToast(String message) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Show long toast message
     */
    protected void showLongToast(String message) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Show network error dialog
     */
    protected void showNetworkError() {
        Context context = getContext();
        if (context != null) {
            DialogUtils.showNetworkError(context);
        }
    }
    
    /**
     * Show confirmation dialog
     */
    protected void showConfirmation(String title, String message, DialogUtils.OnConfirmListener listener) {
        Context context = getContext();
        if (context != null) {
            DialogUtils.showConfirmationDialog(context, title, message, listener);
        }
    }
    
    /**
     * Check if network is available
     */
    protected boolean isNetworkAvailable() {
        Context context = getContext();
        return context != null && NetworkUtils.isNetworkAvailable(context);
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
    public void onDestroyView() {
        hideLoading();
        super.onDestroyView();
    }
}
