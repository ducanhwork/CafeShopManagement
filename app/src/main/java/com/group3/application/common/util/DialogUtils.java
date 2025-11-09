package com.group3.application.common.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Utility class for showing various types of dialogs
 */
public class DialogUtils {
    
    /**
     * Show success dialog with custom message
     */
    public static void showSuccessDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
    
    /**
     * Show error dialog with custom message
     */
    public static void showErrorDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    
    /**
     * Show confirmation dialog with callback
     */
    public static void showConfirmationDialog(Context context, String title, String message, 
                                             final OnConfirmListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onConfirm();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    
    /**
     * Show progress dialog with message
     */
    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }
    
    /**
     * Show network error dialog
     */
    public static void showNetworkError(Context context) {
        showErrorDialog(context, "Network error. Please check your internet connection.");
    }
    
    /**
     * Callback interface for confirmation dialogs
     */
    public interface OnConfirmListener {
        void onConfirm();
    }
}
