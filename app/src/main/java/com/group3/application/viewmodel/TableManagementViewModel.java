package com.group3.application.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.repository.TableRepository;

import java.util.List;

/**
 * ViewModel for Table Management operations
 * Manages table list, CRUD operations, and UI state
 * Uses LiveData for reactive UI updates
 */
public class TableManagementViewModel extends ViewModel {
    
    private final TableRepository repository;
    
    // LiveData for table list
    private final MutableLiveData<List<TableInfo>> tableList = new MutableLiveData<>();
    private final MutableLiveData<TableInfo> currentTable = new MutableLiveData<>();
    
    // LiveData for UI state
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    
    // Filter states
    private final MutableLiveData<String> selectedStatus = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    
    public TableManagementViewModel() {
        this.repository = new TableRepository();
    }
    
    // Getters for LiveData
    public LiveData<List<TableInfo>> getTableList() {
        return tableList;
    }
    
    public LiveData<TableInfo> getCurrentTable() {
        return currentTable;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    
    public LiveData<String> getSelectedStatus() {
        return selectedStatus;
    }
    
    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
    
    /**
     * Load all tables with current filters
     */
    public void loadTables() {
        loadTables(selectedStatus.getValue(), searchQuery.getValue());
    }
    
    /**
     * Load tables with specific filters
     */
    public void loadTables(String status, String search) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        repository.getAllTables(status, search, new BaseRepository.ApiCallback<List<TableInfo>>() {
            @Override
            public void onSuccess(List<TableInfo> result) {
                isLoading.postValue(false);
                tableList.postValue(result);
            }
            
            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
    
    /**
     * Get table by ID
     */
    public void getTableById(String id) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        repository.getTableById(id, new BaseRepository.ApiCallback<TableInfo>() {
            @Override
            public void onSuccess(TableInfo result) {
                isLoading.postValue(false);
                currentTable.postValue(result);
            }
            
            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
    
    /**
     * Create new table
     */
    public void createTable(TableInfo table) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        successMessage.setValue(null);
        
        repository.createTable(table, new BaseRepository.ApiCallback<TableInfo>() {
            @Override
            public void onSuccess(TableInfo result) {
                isLoading.postValue(false);
                successMessage.postValue("Bàn #" + result.getTableNumber() + " đã được tạo thành công");
                // Reload tables to refresh the list
                loadTables();
            }
            
            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
    
    /**
     * Update existing table
     */
    public void updateTable(String id, TableInfo table) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        successMessage.setValue(null);
        
        repository.updateTable(id, table, new BaseRepository.ApiCallback<TableInfo>() {
            @Override
            public void onSuccess(TableInfo result) {
                isLoading.postValue(false);
                successMessage.postValue("Bàn #" + result.getTableNumber() + " đã được cập nhật");
                // Reload tables to refresh the list
                loadTables();
            }
            
            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
    
    /**
     * Delete table
     */
    public void deleteTable(String id) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        successMessage.setValue(null);
        
        repository.deleteTable(id, new BaseRepository.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.postValue(false);
                successMessage.postValue("Bàn đã được xóa thành công");
                // Reload tables to refresh the list
                loadTables();
            }
            
            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
    
    /**
     * Update table status only
     */
    public void updateTableStatus(String id, String newStatus) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        successMessage.setValue(null);
        
        repository.updateTableStatus(id, newStatus, new BaseRepository.ApiCallback<TableInfo>() {
            @Override
            public void onSuccess(TableInfo result) {
                isLoading.postValue(false);
                successMessage.postValue("Trạng thái bàn #" + result.getTableNumber() + " đã được cập nhật");
                // Reload tables to refresh the list
                loadTables();
            }
            
            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
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
        searchQuery.setValue(null);
        loadTables();
    }
    
    /**
     * Clear error/success messages
     */
    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
}
