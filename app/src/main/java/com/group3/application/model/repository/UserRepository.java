package com.group3.application.model.repository;

import com.group3.application.model.dto.UserCreateRequest;
import com.group3.application.model.entity.Role;
import com.group3.application.model.entity.User;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;


public class UserRepository {

    private ApiService apiService;

    public UserRepository() {
        apiService = ApiClient.get().create(ApiService.class);
    }

    public Call<List<User>> getAllUsers() {
        return apiService.getAllUsers();
    }

    public Call<UserCreateRequest> createUser(UserCreateRequest newStaff) {
        return apiService.createUser(newStaff);
    }

    public Call<User> updateUser(User staff) {
        return apiService.updateUser(staff.getId().toString(), staff);
    }

    public Call<List<Role>> getRoles() {
        return apiService.getRoles();
    }
}
