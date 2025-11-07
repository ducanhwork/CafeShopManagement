package com.group3.application.model.repository;

import com.group3.application.model.entity.User;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;


public class UserRepository {
    private final ApiService api = ApiClient.get().create(ApiService.class);
//    public Call<User> getMyProfile() {
//        return api.myProfile();
//    }
}
