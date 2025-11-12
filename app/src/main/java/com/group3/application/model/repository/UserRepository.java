package com.group3.application.model.repository;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.group3.application.model.dto.UserCreateRequest;
import com.group3.application.model.dto.UserUpdateRequest;
import com.group3.application.model.entity.Role;
import com.group3.application.model.entity.User;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import retrofit2.Call;


public class UserRepository {
    private static final String TAG = "UserRepository";
    private final ApiService apiService;
    private final SharedPreferences sharedPreferences;

    public UserRepository(){
        this.apiService = ApiClient.get().create(ApiService.class);
        this.sharedPreferences = null;
    }

    public UserRepository(Application application) {
        this.apiService = ApiClient.get().create(ApiService.class);
        this.sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void getAllUsers(OnUsersFetchListener listener) {
        String authToken = sharedPreferences.getString(KEY_AUTH_TOKEN, null);
        if (authToken == null || authToken.isEmpty()) {
            listener.onUsersFetchComplete(null, "Lỗi: Người dùng chưa đăng nhập.");
            return;
        }
        String finalAuthToken = "Bearer " + authToken;

        apiService.getAllUsers(finalAuthToken).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onUsersFetchComplete(response.body(), null);
                } else {
                    String error = "Lỗi lấy danh sách nhân viên. Mã: " + response.code();
                    Log.e(TAG, error);
                    listener.onUsersFetchComplete(null, error);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                listener.onUsersFetchComplete(null, "Lỗi kết nối mạng.");
            }
        });
    }

    public interface OnUsersFetchListener {
        void onUsersFetchComplete(List<User> users, String error);
    }

    public Call<List<User>> getAllUsers() {
        return apiService.getAllUsers();
    }


    public Call<List<Role>> getRoles() {
        return apiService.getRoles();
    }

    public Call<UserCreateRequest> createUser(UserCreateRequest newStaff) {
        return apiService.createUser(newStaff);
    }

    public Call<UserUpdateRequest> updateUser(UserUpdateRequest staff) {
        return apiService.updateUser(staff.getId(), staff);
    }
}
