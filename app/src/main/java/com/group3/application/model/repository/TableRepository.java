package com.group3.application.model.repository;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.model.bean.BulkCreateResponse;
import com.group3.application.model.bean.CreateTableRequest;
import com.group3.application.model.bean.UpdateTableRequest;
import com.group3.application.model.bean.UpdateTableStatusRequest;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.TableApiService;

import java.util.List;

import retrofit2.Call;

/**
 * Repository for table-related API operations
 * Handles JWT token and executes API calls
 */
public class TableRepository extends BaseRepository {
    private final TableApiService api;

    public TableRepository() {
        this.api = ApiClient.get().create(TableApiService.class);
    }

    /**
     * Get all tables with optional filters
     * @param token JWT Bearer token (format: "Bearer {token}")
     * @param status Filter by status (Available, Occupied, Reserved)
     * @param location Filter by location (Indoor, Outdoor, Balcony)
     * @param minSheetCount Minimum number of seats
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param sort Sort field,direction (e.g., "name,asc")
     */
    public void getTables(String token, String status, String location, Integer minSeatCount,
                          Integer page, Integer size, String sort, ApiCallback<List<TableInfo>> callback) {
        Call<List<TableInfo>> call = api.getTables(status, location, minSeatCount, page, size, sort, token);
        executeCall(call, callback);
    }
    
    /**
     * Get table by ID
     */
    public void getTableById(String token, String id, ApiCallback<TableInfo> callback) {
        Call<TableInfo> call = api.getTableById(id, token);
        executeCall(call, callback);
    }
    
    /**
     * Create new table (Waiter, Manager only)
     */
    public void createTable(String token, CreateTableRequest request, ApiCallback<TableInfo> callback) {
        Call<TableInfo> call = api.createTable(request, token);
        executeCall(call, callback);
    }
    
    /**
     * Update existing table (Waiter, Manager only)
     */
    public void updateTable(String token, String id, UpdateTableRequest request, ApiCallback<TableInfo> callback) {
        Call<TableInfo> call = api.updateTable(id, request, token);
        executeCall(call, callback);
    }
    
    /**
     * Quick update table status only (Waiter, Manager)
     */
    public void updateTableStatus(String token, String id, String status, ApiCallback<TableInfo> callback) {
        UpdateTableStatusRequest request = new UpdateTableStatusRequest(status);
        Call<TableInfo> call = api.updateTableStatus(id, request, token);
        executeCall(call, callback);
    }
    
    /**
     * Delete table (Manager only)
     */
    public void deleteTable(String token, String id, ApiCallback<Void> callback) {
        Call<Void> call = api.deleteTable(id, token);
        executeCall(call, callback);
    }
    
    /**
     * Bulk create tables (Manager only)
     */
    public void bulkCreateTables(String token, List<CreateTableRequest> requests, ApiCallback<BulkCreateResponse> callback) {
        Call<BulkCreateResponse> call = api.bulkCreateTables(requests, token);
        executeCall(call, callback);
    }
    
    /**
     * Get available table count
     */
    public void getAvailableTableCount(String token, ApiCallback<Integer> callback) {
        Call<Integer> call = api.getAvailableTableCount(token);
        executeCall(call, callback);
    }
}
