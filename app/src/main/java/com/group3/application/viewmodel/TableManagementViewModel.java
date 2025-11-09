package com.group3.application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.common.util.PreferenceManager;
import com.group3.application.model.bean.CreateTableRequest;
import com.group3.application.model.bean.UpdateTableRequest;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.repository.TableRepository;

import java.util.List;

/**
 * ViewModel for Table Management operations
 * Manages table list, CRUD operations, and UI state
 * Uses LiveData for reactive UI updates
 */
public class TableManagementViewModel extends AndroidViewModel {
    
    private final TableRepository repository;
    
    // LiveData for table list
    private final MutableLiveData<List<TableInfo>> tableList = new MutableLiveData<>();
    private final MutableLiveData<TableInfo> currentTable = new MutableLiveData<>();
    
    // LiveData for UI state
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    
    // Filter states
    private final MutableLiveData<String> selectedStatus = new MutableLiveData<>();
    private final MutableLiveData<String> selectedLocation = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    
    public TableManagementViewModel(@NonNull Application application) {
        super(application);
        this.repository = new TableRepository();
    }
    
    // Getters for LiveData
    public LiveData<List<TableInfo>> getTableList() {
        return tableList;
    }
    
    public LiveData<TableInfo> getCurrentTable() {
        return currentTable;
    }
    
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    public LiveData<String> getError() {
        return error;
    }
    
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    
    public LiveData<String> getSelectedStatus() {
        return selectedStatus;
    }
    
    public LiveData<String> getSelectedLocation() {
        return selectedLocation;
    }
    
    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
    
    /**
     * Get JWT token with Bearer prefix
     */
    private String getAuthToken() {
        String rawToken = PreferenceManager.getToken(getApplication());
        return rawToken != null ? "Bearer " + rawToken : null;
    }
    
    /**
     * Load all tables with current filters
     */
    public void loadTables() {
        loadTables(selectedStatus.getValue(), selectedLocation.getValue());
    }
    
    /**
     * Load tables with specific filters
     */
    public void loadTables(String status, String location) {
        loading.setValue(true);
        error.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.getTables(token, status, location, null, 0, 100, null, 
            new BaseRepository.ApiCallback<List<TableInfo>>() {
                @Override
                public void onSuccess(List<TableInfo> result) {
                    loading.postValue(false);
                    tableList.postValue(result);
                }
                
                @Override
                public void onError(String errorMsg) {
                    loading.postValue(false);
                    error.postValue(errorMsg);
                }
            });
    }
    
    /**
     * Get table by ID
     */
    public void getTableById(String id) {
        loading.setValue(true);
        error.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.getTableById(token, id, new BaseRepository.ApiCallback<TableInfo>() {
            @Override
            public void onSuccess(TableInfo result) {
                loading.postValue(false);
                currentTable.postValue(result);
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Create new table
     */
    public void createTable(String name, String location, Integer seatCount, String status) {
        loading.setValue(true);
        error.setValue(null);
        successMessage.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        CreateTableRequest request = new CreateTableRequest(name, location, seatCount, status);
        
        repository.createTable(token, request, new BaseRepository.ApiCallback<TableInfo>() {
            @Override
            public void onSuccess(TableInfo result) {
                loading.postValue(false);
                successMessage.postValue("Table " + result.getName() + " created successfully");
                // Reload tables to refresh the list
                loadTables();
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Update existing table
     */
    public void updateTable(String id, String name, String location, Integer seatCount, String status) {
        loading.setValue(true);
        error.setValue(null);
        successMessage.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        UpdateTableRequest request = new UpdateTableRequest(name, location, seatCount, status);
        
        repository.updateTable(token, id, request, new BaseRepository.ApiCallback<TableInfo>() {
            @Override
            public void onSuccess(TableInfo result) {
                loading.postValue(false);
                successMessage.postValue("Table " + result.getName() + " updated successfully");
                // Reload tables to refresh the list
                loadTables();
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Delete table
     */
    public void deleteTable(String id) {
        loading.setValue(true);
        error.setValue(null);
        successMessage.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.deleteTable(token, id, new BaseRepository.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loading.postValue(false);
                successMessage.postValue("Table deleted successfully");
                // Reload tables to refresh the list
                loadTables();
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Update table status only
     */
    public void updateTableStatus(String id, String newStatus) {
        loading.setValue(true);
        error.setValue(null);
        successMessage.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.updateTableStatus(token, id, newStatus, new BaseRepository.ApiCallback<TableInfo>() {
            @Override
            public void onSuccess(TableInfo result) {
                loading.postValue(false);
                successMessage.postValue("Table " + result.getName() + " status updated");
                // Reload tables to refresh the list
                loadTables();
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Set status filter
     */
    public void setStatusFilter(String status) {
        selectedStatus.setValue(status);
        loadTables();
    }
    
    /**
     * Set location filter
     */
    public void setLocationFilter(String location) {
        selectedLocation.setValue(location);
        loadTables();
    }
    
    /**
     * Set search query
     */
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        loadTables();
    }
    
    /**
     * Clear all filters
     */
    public void clearFilters() {
        selectedStatus.setValue(null);
        selectedLocation.setValue(null);
        searchQuery.setValue(null);
        loadTables();
    }
    
    /**
     * Clear error/success messages
     */
    public void clearMessages() {
        error.setValue(null);
        successMessage.setValue(null);
    }
}
