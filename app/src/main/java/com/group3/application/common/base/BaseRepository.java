package com.group3.application.common.base;

import android.util.Log;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Base Repository class with common API handling
 */
public abstract class BaseRepository {
    
    protected static final String TAG = "BaseRepository";
    
    /**
     * Callback interface for API responses
     */
    public interface ApiCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
    
    /**
     * Execute API call with standard error handling
     */
    protected <T> void executeCall(@NonNull Call<T> call, @NonNull ApiCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = handleErrorResponse(response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed", t);
                String error = handleFailure(t);
                callback.onError(error);
            }
        });
    }
    
    /**
     * Handle error response from server
     */
    private <T> String handleErrorResponse(Response<T> response) {
        try {
            String errorBody = response.errorBody() != null 
                ? response.errorBody().string() 
                : "Unknown error";
            
            switch (response.code()) {
                case 400:
                    return "Bad request: " + errorBody;
                case 401:
                    return "Unauthorized. Please login again.";
                case 403:
                    return "Access denied. You don't have permission.";
                case 404:
                    return "Resource not found.";
                case 409:
                    return "Conflict: " + errorBody;
                case 500:
                    return "Server error. Please try again later.";
                default:
                    return "Error: " + errorBody;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response", e);
            return "Request failed with code: " + response.code();
        }
    }
    
    /**
     * Handle network/connection failures
     */
    private String handleFailure(Throwable t) {
        String message = t.getMessage();
        
        if (message != null) {
            if (message.contains("timeout")) {
                return "Request timeout. Please check your connection.";
            } else if (message.contains("Unable to resolve host")) {
                return "Cannot connect to server. Please check your internet connection.";
            } else if (message.contains("Failed to connect")) {
                return "Connection failed. Please try again.";
            }
        }
        
        return "Network error: " + (message != null ? message : "Unknown error");
    }
    
    /**
     * Log error for debugging
     */
    protected void logError(String method, String error) {
        Log.e(TAG, method + " failed: " + error);
    }
    
    /**
     * Log success for debugging
     */
    protected void logSuccess(String method) {
        Log.d(TAG, method + " succeeded");
    }
}
