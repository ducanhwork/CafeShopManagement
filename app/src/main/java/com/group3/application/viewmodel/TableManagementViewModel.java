package com.group3.application.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.dto.CreateTableRequest;
import com.group3.application.model.dto.PageResponse;
import com.group3.application.model.dto.UpdateTableRequest;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.repository.TableRepository;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableManagementViewModel extends AndroidViewModel {
    private static final String TAG = "TableManagementVM";
    private final TableRepository tableRepository;

    private final MutableLiveData<List<TableInfo>> tables = new MutableLiveData<>();
    private final MutableLiveData<TableInfo> selectedTable = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public TableManagementViewModel(@NonNull Application application) {
        super(application);
        tableRepository = new TableRepository(application.getApplicationContext());
    }

    // LiveData Getters
    public LiveData<List<TableInfo>> getTables() { return tables; }
    public LiveData<TableInfo> getSelectedTable() { return selectedTable; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getSuccessMessage() { return successMessage; }

    // Fetch all tables with pagination (Manager view)
    public void fetchAllTables(String status, String location, Integer minSeatCount, 
                               Integer page, Integer size, String sort) {
        isLoading.setValue(true);
        error.setValue(null);
        
        tableRepository.getAllTablesWithPagination(status, location, minSeatCount, page, size, sort)
                .enqueue(new Callback<PageResponse<TableInfo>>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<TableInfo>> call, @NonNull Response<PageResponse<TableInfo>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the content list from the paginated response
                    tables.setValue(response.body().getContent());
                } else {
                    error.setValue("Failed to fetch tables: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<TableInfo>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error fetching tables", t);
            }
        });
    }

    // Get table by ID
    public void fetchTableById(String id) {
        isLoading.setValue(true);
        error.setValue(null);
        
        tableRepository.getTableById(id).enqueue(new Callback<TableInfo>() {
            @Override
            public void onResponse(@NonNull Call<TableInfo> call, @NonNull Response<TableInfo> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    selectedTable.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch table: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TableInfo> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error fetching table", t);
            }
        });
    }

    // Create new table
    public void createTable(String name, String location, Integer seatCount, String status) {
        isLoading.setValue(true);
        error.setValue(null);
        
        CreateTableRequest request = new CreateTableRequest(name, location, seatCount, status);
        tableRepository.createTable(request).enqueue(new Callback<TableInfo>() {
            @Override
            public void onResponse(@NonNull Call<TableInfo> call, @NonNull Response<TableInfo> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    successMessage.setValue("Table created successfully");
                    fetchAllTables(null, null, null, 0, 20, "name,asc");
                } else {
                    error.setValue("Failed to create table: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TableInfo> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error creating table", t);
            }
        });
    }

    // Update table
    public void updateTable(String id, String name, String location, Integer seatCount, String status) {
        isLoading.setValue(true);
        error.setValue(null);
        
        UpdateTableRequest request = new UpdateTableRequest(name, location, seatCount, status);
        tableRepository.updateTable(id, request).enqueue(new Callback<TableInfo>() {
            @Override
            public void onResponse(@NonNull Call<TableInfo> call, @NonNull Response<TableInfo> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    successMessage.setValue("Table updated successfully");
                    fetchAllTables(null, null, null, 0, 20, "name,asc");
                } else {
                    error.setValue("Failed to update table: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TableInfo> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error updating table", t);
            }
        });
    }

    // Delete table
    public void deleteTable(String id) {
        isLoading.setValue(true);
        error.setValue(null);
        
        tableRepository.deleteTable(id).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    successMessage.setValue("Table deleted successfully");
                    fetchAllTables(null, null, null, 0, 20, "name,asc");
                } else {
                    error.setValue("Failed to delete table: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error deleting table", t);
            }
        });
    }
}
