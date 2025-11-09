package com.group3.application.model.repository;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.model.dto.AuthResponse;
import com.group3.application.model.dto.LoginRequest;
import com.group3.application.model.dto.RegisterRequest;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import retrofit2.Call;

/**
 * Repository for authentication-related API operations
 */
public class AuthRepository extends BaseRepository {
    private final ApiService api = ApiClient.get().create(ApiService.class);
    
    /**
     * Login user
     */
    public void login(String email, String password, ApiCallback<AuthResponse> callback) {
        LoginRequest request = new LoginRequest(email, password);
        Call<AuthResponse> call = api.login(request);
        executeCall(call, callback);
    }
    
    /**
     * Register new user
     */
    public void register(String name, String email, String password, String role, 
                        ApiCallback<AuthResponse> callback) {
        RegisterRequest request = new RegisterRequest(name, email, password, role);
        Call<AuthResponse> call = api.register(request);
        executeCall(call, callback);
    }
    
    /**
     * Logout current user
     */
    public void logout(ApiCallback<Void> callback) {
        Call<Void> call = api.logout();
        executeCall(call, callback);
    }
    
    /**
     * Get current user info
     */
    public void getCurrentUser(ApiCallback<AuthResponse.UserInfo> callback) {
        Call<AuthResponse.UserInfo> call = api.getCurrentUser();
        executeCall(call, callback);
    }
}
