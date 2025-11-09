package com.group3.application.model.repository;

import com.group3.application.model.entity.Product;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;

public class ProductRepository {
    private final ApiService api = ApiClient.get().create(ApiService.class);

    public Call<List<Product>> getProducts(String status, String categoryId, String keyword) {
        return api.listProducts(status, categoryId, keyword);
    }
}
