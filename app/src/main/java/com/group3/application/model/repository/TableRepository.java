package com.group3.application.model.repository;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;

/**
 * Repository for table-related API operations
 * Extends BaseRepository for standard error handling
 */
public class TableRepository extends BaseRepository {
    private final ApiService api = ApiClient.get().create(ApiService.class);

    /**
     * Get all tables with optional filters
     */
    public void getTables(String status, String keyword, ApiCallback<List<TableInfo>> callback) {
        Call<List<TableInfo>> call = api.listTables(status, keyword);
        executeCall(call, callback);
    }
    
    /**
     * Get table by ID
     */
    public void getTableById(Long id, ApiCallback<TableInfo> callback) {
        Call<TableInfo> call = api.getTable(id);
        executeCall(call, callback);
    }
    
    /**
     * Create new table
     */
    public void createTable(TableInfo table, ApiCallback<TableInfo> callback) {
        Call<TableInfo> call = api.createTable(table);
        executeCall(call, callback);
    }
    
    /**
     * Update existing table
     */
    public void updateTable(Long id, TableInfo table, ApiCallback<TableInfo> callback) {
        Call<TableInfo> call = api.updateTable(id, table);
        executeCall(call, callback);
    }
    
    /**
     * Delete table
     */
    public void deleteTable(Long id, ApiCallback<Void> callback) {
        Call<Void> call = api.deleteTable(id);
        executeCall(call, callback);
    }
}
