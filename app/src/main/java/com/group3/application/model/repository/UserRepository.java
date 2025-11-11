package com.group3.application.model.repository;

import com.group3.application.model.entity.Role;
import com.group3.application.model.entity.User;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;


public class UserRepository {
    private final ApiService api = ApiClient.get().create(ApiService.class);
//    public Call<User> getMyProfile() {
//        return api.myProfile();
//    }

    public Call<List<User>> getAllUsers() {
        return api.getAllUsers();
    }

    public Call<User> createUser(User newStaff) {
        return api.createUser(newStaff);
    }

    public Call<User> updateUser(User staff) {
        return api.updateUser(staff.getId().toString(), staff);
    }

    public Call<List<Role>> getRoles() {
        return api.getRoles();
    }
}
