package com.group3.application.model.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.group3.application.model.dto.CreateTableRequest;
import com.group3.application.model.dto.PageResponse;
import com.group3.application.model.dto.UpdateTableRequest;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class TableRepository {

    private final ApiService api = ApiClient.get().create(ApiService.class);
    private SharedPreferences sharedPreferences;

    public TableRepository() {
    }

    public TableRepository(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private String getAuthToken() {
        if (sharedPreferences != null) {
            String token = sharedPreferences.getString(KEY_AUTH_TOKEN, "");
            if (!token.isEmpty()) {
                return token.startsWith("Bearer ") ? token : "Bearer " + token;
            }
        }
        return "";
    }

    // List tables with filters
    public Call<List<TableInfo>> getTables(String status, String keyword) {
        return api.listTables(status, keyword);
    }

    // Get all tables with pagination (for manager)
    public Call<PageResponse<TableInfo>> getAllTablesWithPagination(String status, String location, 
                                                             Integer minSeatCount, Integer page, 
                                                             Integer size, String sort) {
        return api.getAllTablesWithPagination(getAuthToken(), status, location, minSeatCount, page, size, sort);
    }

    // Get table by ID
    public Call<TableInfo> getTableById(String id) {
        return api.getTableById(getAuthToken(), id);
    }

    // Create new table
    public Call<TableInfo> createTable(CreateTableRequest request) {
        return api.createTable(getAuthToken(), request);
    }

    // Update table
    public Call<TableInfo> updateTable(String id, UpdateTableRequest request) {
        return api.updateTable(getAuthToken(), id, request);
    }

    // Update table status
    public Call<TableInfo> updateTableStatus(String id, Map<String, String> status) {
        return api.updateTableStatus(getAuthToken(), id, status);
    }

    // Delete table
    public Call<Map<String, String>> deleteTable(String id) {
        return api.deleteTable(getAuthToken(), id);
    }
}
