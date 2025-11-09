package com.group3.application.model.repository;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.KEY_IS_LOGGED_IN;
import static com.group3.application.viewmodel.LoginViewModel.KEY_USER_EMAIL;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Callback;

import com.google.gson.Gson;
import com.group3.application.common.constant.IConstants;
import com.group3.application.model.dto.AuthenticationRequest;
import com.group3.application.model.dto.AuthenticationResponse;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.UpdatePassWordRequest;
import com.group3.application.model.entity.User;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.io.IOException;

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
                        listener.onLoginComplete(new APIResult(
                                true,
                                "Đăng nhập thành công!",
                                token
                        ));
                    } else {
                        String errorMessage = "Đăng nhập thất bại: Token không hợp lệ từ server.";
                        listener.onLoginComplete(new APIResult(false, errorMessage, null));
                        Log.e(TAG, "Login successful but no token: " + response.message());
                    }
                } else {
                    String errorMessage = "";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    Gson gson = new Gson();
                    APIResult apiResult = gson.fromJson(errorMessage, APIResult.class);
                    Log.e(TAG, "Login API failed: " + apiResult.getMessage());
                    listener.onLoginComplete(apiResult);
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                Log.e(TAG, "Login API network error: " + t.getMessage(), t);
                listener.onLoginComplete(new APIResult(false, "Lỗi kết nối mạng: " + t.getMessage(), null));
            }
        });
    }

    private void saveAuthToken(String token) {
        SharedPreferences.Editor editor = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
        Log.d(TAG, "Auth token saved: " + token);
    }

    public boolean isUserLoggedIn() {
        SharedPreferences prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && (prefs.getString(KEY_AUTH_TOKEN, null) != null);
    }


    public void forgotPassword(String email, OnResetPasswordCompleteListener listener) {
        apiService.resetPassword(email).enqueue(new Callback<APIResult>() {

            @Override
            public void onResponse(Call<APIResult> call, Response<APIResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResult result = response.body();
                    if (result != null) {
                        String message = result.getMessage();
                        Log.d(TAG, "Password reset successful: " + message);
                        listener.onResetPasswordComplete(new APIResult(true, message, null));
                    }
                } else {
                    String errorResponse = "";
                    try {
                        if (response.errorBody() != null) {
                            errorResponse = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    Gson gson = new Gson();
                    APIResult apiResult = gson.fromJson(errorResponse, APIResult.class);

                    Log.e(TAG, "Login API failed: " + apiResult.getMessage());

                    listener.onResetPasswordComplete(new APIResult(false, apiResult.getMessage(), null));
                }
            }

            @Override
            public void onFailure(Call<APIResult> call, Throwable t) {
                Log.e(TAG, "Login API network error: " + t.getMessage(), t);
                listener.onResetPasswordComplete(new APIResult(false, "Line 131 Auth Repo: " + t.getMessage(), null));
            }
        });
    }

    public void getMyProfile(OnGetProfileCompleteListener listener) {
        apiService.myProfile("Bearer " + application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_AUTH_TOKEN, null)).enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (user != null) {
                        listener.onGetProfileComplete(new APIResult(true, "Get profile successfully", user));
                    }
                } else {
                    String errorMessage = "Get profile failed. Error code: " + response.code();
                    Log.e(TAG, "Get profile failed: " + errorMessage);
                    listener.onGetProfileComplete(new APIResult(false, errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Log.e(TAG, "onFailure get profile: " + throwable.getMessage());
                listener.onGetProfileComplete(new APIResult(false, "onFailure get profile: " + throwable.getMessage()));
            }
        });
    }

    public void changePassword(String currentPassword, String newPassword, String confirmPassword, onChangePasswordListener listener) {
        //extract email
        SharedPreferences prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String email = prefs.getString(KEY_USER_EMAIL, null);
        if (email == null) {
            listener.onChangePasswordComplete(new APIResult(false, IConstants.EMAIL_NOT_IN_PREFS, null));
            return;
        }
        UpdatePassWordRequest request = new UpdatePassWordRequest(email, currentPassword, newPassword);
        apiService.changePassword(request).enqueue(new Callback<APIResult>() {
            @Override
            public void onResponse(Call<APIResult> call, Response<APIResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    APIResult result = response.body();
                    if (result != null) {
                        if (result.getData() != null) {
                            Log.d(TAG, "Password changed successfully.");
                            listener.onChangePasswordComplete(new APIResult(true, IConstants.CHANGED_PASSWORD_SUCCESS, null));
                        }
                        if (result.getMessage() != null) {
                            Log.e(TAG, "Change password failed: " + result.getMessage());
                            listener.onChangePasswordComplete(new APIResult(false, result.getMessage(), null));
                        }
                    } else {
                        Log.e(TAG, "Change password failed: Response body is null.");
                    }
                } else {
                    String errorResponse = null;
                    try {
                        errorResponse = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Gson gson = new Gson();
                    APIResult apiResult = gson.fromJson(errorResponse, APIResult.class);
                    String errorMessage = apiResult.getMessage();
                    listener.onChangePasswordComplete(new APIResult(false, errorMessage, null));
                    Log.e(TAG, "Change password failed: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<APIResult> call, Throwable throwable) {
                Log.e(TAG, "onFailure change password: " + throwable.getMessage());
                listener.onChangePasswordComplete(new APIResult(false, IConstants.CHANGED_PASSWORD_FAILED, null));
            }
        });
    }

    public interface OnLoginCompleteListener {
        void onLoginComplete(APIResult result);
    }

    public interface OnResetPasswordCompleteListener {
        void onResetPasswordComplete(APIResult result);
    }

    public interface OnGetProfileCompleteListener {
        void onGetProfileComplete(APIResult result);
    }

    public interface onChangePasswordListener {
        void onChangePasswordComplete(APIResult result);
    }

}
