package com.group3.application.model.repository;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.KEY_IS_LOGGED_IN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import retrofit2.Callback;

import com.group3.application.model.dto.AuthenticationRequest;
import com.group3.application.model.dto.AuthenticationResponse;
import com.group3.application.model.dto.LoginResult;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import retrofit2.Call;
import retrofit2.Response;

public class AuthRepository {
    private final ApiService apiService;
    private final Application application;
    private static final String TAG = "AuthRepository";

    public AuthRepository(Application application) {
        this.application = application;
        this.apiService = ApiClient.get().create(ApiService.class);
    }

    public void login(String email, String password, OnLoginCompleteListener listener) {
        AuthenticationRequest request = new AuthenticationRequest(email, password);

        apiService.login(request).enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthenticationResponse authResponse = response.body();
                    String token = authResponse.getToken();

                    if (token != null && !token.isEmpty()) {
                        saveAuthToken(token);
                        // Tạo LoginResult thành công với token và email đã nhập
                        listener.onLoginComplete(new LoginResult(
                                true,
                                "Đăng nhập thành công!",
                                token,
                                email // Sử dụng email đã nhập
                        ));
                    } else {
                        // Phản hồi thành công (HTTP 200) nhưng token bị thiếu hoặc rỗng
                        String errorMessage = "Đăng nhập thất bại: Token không hợp lệ từ server.";
                        listener.onLoginComplete(new LoginResult(false, errorMessage, null, null));
                        Log.e(TAG, "Login successful but no token: " + response.message());
                    }
                } else {
                    // Phản hồi HTTP không thành công (4xx, 5xx)
                    String errorMessage = "Đăng nhập thất bại. Mã lỗi: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            // Cố gắng đọc thông báo lỗi từ errorBody
                            errorMessage += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    Log.e(TAG, "Login API failed: " + errorMessage);
                    listener.onLoginComplete(new LoginResult(false, errorMessage, null, null));
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                // Lỗi mạng hoặc lỗi không xác định
                Log.e(TAG, "Login API network error: " + t.getMessage(), t);
                listener.onLoginComplete(new LoginResult(false, "Lỗi kết nối mạng: " + t.getMessage(), null, null));
            }
        });
    }

    private void saveAuthToken(String token) {
        SharedPreferences.Editor editor = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.putBoolean(KEY_IS_LOGGED_IN, true); // Đảm bảo cờ loggedIn cũng được lưu
        editor.apply();
        Log.d(TAG, "Auth token saved: " + token);
    }

    public boolean isUserLoggedIn() {
        SharedPreferences prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && (prefs.getString(KEY_AUTH_TOKEN, null) != null);
    }

    public interface OnLoginCompleteListener {
        void onLoginComplete(LoginResult result);
    }
}
