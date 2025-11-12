package com.group3.application.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.model.dto.UserCreateRequest;
import com.group3.application.model.dto.UserUpdateRequest;
import com.group3.application.model.entity.Role;
import com.group3.application.model.entity.User;
import com.group3.application.model.repository.UserRepository;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffListViewModel extends ViewModel {

    private UserRepository staffRepository = new UserRepository();

    private final MutableLiveData<List<User>> staffs = new MutableLiveData<>();
    private final MutableLiveData<List<Role>> roles = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<UserCreateRequest> createdStaff = new MutableLiveData<>();
    private final MutableLiveData<UserUpdateRequest> updatedStaff = new MutableLiveData<>();

    public LiveData<List<User>> getStaffs() {
        return staffs;
    }

    public LiveData<List<Role>> getRoles() {
        return roles;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<UserCreateRequest> getCreatedStaff() {
        return createdStaff;
    }

    public LiveData<UserUpdateRequest> getUpdatedStaff() {
        return updatedStaff;
    }

    public void fetchStaff() {
        isLoading.setValue(true);
        staffRepository.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    staffs.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch staffs");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                error.setValue(throwable.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void fetchRoles() {
        isLoading.setValue(true);
        staffRepository.getRoles().enqueue(new Callback<List<Role>>() {
            @Override
            public void onResponse(Call<List<Role>> call, Response<List<Role>> response) {
                if (response.isSuccessful()) {
                    roles.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch roles");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Role>> call, Throwable t) {
                error.setValue(t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void addStaff(String email, String password, String fullname, String mobile, String role) {
        isLoading.setValue(true);
        UserCreateRequest newStaff = new UserCreateRequest(email, password, fullname, mobile, role);

        staffRepository.createUser(newStaff).enqueue(new Callback<UserCreateRequest>() {
            @Override
            public void onResponse(Call<UserCreateRequest> call, Response<UserCreateRequest> response) {
                if (response.isSuccessful()) {
                    createdStaff.setValue(response.body());
                } else {
                    try {
                        error.setValue("Failed to create staff: " + response.errorBody().string());
                    } catch (IOException e) {
                        error.setValue("Failed to create staff and could not parse error body.");
                    }
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<UserCreateRequest> call, Throwable t) {
                error.setValue(t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void updateUser(UserUpdateRequest staff) {
        isLoading.setValue(true);
        staffRepository.updateUser(staff).enqueue(new Callback<UserUpdateRequest>() {
            @Override
            public void onResponse(Call<UserUpdateRequest> call, Response<UserUpdateRequest> response) {
                if (response.isSuccessful()) {
                    updatedStaff.setValue(response.body());
                } else {
                    try {
                        error.setValue("Failed to update staff: " + response.errorBody().string());
                    } catch (IOException e) {
                        error.setValue("Failed to update staff and could not parse error body.");
                    }
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<UserUpdateRequest> call, Throwable t) {
                error.setValue(t.getMessage());
                isLoading.setValue(false);
            }
        });
    }
}
