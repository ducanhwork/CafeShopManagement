package com.group3.application.model.repository;

import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;

public class TableRepository {
    private final ApiService api = ApiClient.get().create(ApiService.class);

    public Call<List<TableInfo>> getTables(String status, String keyword) {
        return api.listTables(status, keyword);
    }
}
