package com.group3.application.model.repository;

import com.group3.application.model.entity.Category;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;

public class CategoryRepository {
    private final ApiService api = ApiClient.get().create(ApiService.class);

    public Call<List<Category>> getCategories() {
        return api.getCategories();
    }
}
