package com.group3.application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.common.util.PreferenceManager;
import com.group3.application.model.bean.AddStockRequest;
import com.group3.application.model.bean.CreateIngredientRequest;
import com.group3.application.model.bean.UpdateIngredientRequest;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.model.entity.LowStockAlert;
import com.group3.application.model.entity.StockTransaction;
import com.group3.application.model.repository.InventoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewModel for Inventory Management operations
 * Manages ingredient list, stock operations, and UI state
 */
public class InventoryViewModel extends AndroidViewModel {
    
    private final InventoryRepository repository;
    
    // LiveData for ingredient list
    private final MutableLiveData<List<Ingredient>> ingredientList = new MutableLiveData<>();
    private final MutableLiveData<Ingredient> currentIngredient = new MutableLiveData<>();
    private final MutableLiveData<List<LowStockAlert>> lowStockAlerts = new MutableLiveData<>();
    private final MutableLiveData<List<StockTransaction>> transactionHistory = new MutableLiveData<>();
    
    // LiveData for UI state
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    
    // Filter states
    private final MutableLiveData<String> stockFilter = new MutableLiveData<>(); // "all", "low_stock", "out_of_stock"
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    
    // Cache for filtering
    private List<Ingredient> allIngredients = new ArrayList<>();
    
    public InventoryViewModel(@NonNull Application application) {
        super(application);
        this.repository = new InventoryRepository();
    }
    
    // Getters for LiveData
    public LiveData<List<Ingredient>> getIngredientList() {
        return ingredientList;
    }
    
    public LiveData<Ingredient> getCurrentIngredient() {
        return currentIngredient;
    }
    
    public LiveData<List<LowStockAlert>> getLowStockAlerts() {
        return lowStockAlerts;
    }
    
    public LiveData<List<StockTransaction>> getTransactionHistory() {
        return transactionHistory;
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
    
    public LiveData<String> getStockFilter() {
        return stockFilter;
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
     * Load all ingredients
     */
    public void loadIngredients() {
        loading.setValue(true);
        error.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.getAllIngredients(token, new BaseRepository.ApiCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> result) {
                loading.postValue(false);
                allIngredients = result;
                applyFilters();
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Search ingredients by name
     */
    public void searchIngredients(String name) {
        if (name == null || name.trim().isEmpty()) {
            loadIngredients();
            return;
        }
        
        loading.setValue(true);
        error.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.searchIngredients(token, name, new BaseRepository.ApiCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> result) {
                loading.postValue(false);
                allIngredients = result;
                applyFilters();
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Get ingredient by ID
     */
    public void getIngredientById(String id) {
        loading.setValue(true);
        error.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.getIngredientById(token, id, new BaseRepository.ApiCallback<Ingredient>() {
            @Override
            public void onSuccess(Ingredient result) {
                loading.postValue(false);
                currentIngredient.postValue(result);
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Create new ingredient (Manager only)
     */
    public void createIngredient(String name, String description, Double price, Integer reorderLevel, 
                                 String imageLink, String status) {
        loading.setValue(true);
        error.setValue(null);
        successMessage.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        CreateIngredientRequest request = new CreateIngredientRequest(
                name, description, price, reorderLevel, imageLink, status
        );
        
        repository.createIngredient(token, request, new BaseRepository.ApiCallback<Ingredient>() {
            @Override
            public void onSuccess(Ingredient result) {
                loading.postValue(false);
                successMessage.postValue("Ingredient '" + result.getName() + "' created successfully");
                loadIngredients(); // Reload list
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Update existing ingredient (Manager only)
     */
    public void updateIngredient(String id, String name, String description, Double price, 
                                Integer reorderLevel, String imageLink, String status) {
        loading.setValue(true);
        error.setValue(null);
        successMessage.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        UpdateIngredientRequest request = new UpdateIngredientRequest(
                name, description, price, reorderLevel, imageLink, status
        );
        
        repository.updateIngredient(token, id, request, new BaseRepository.ApiCallback<Ingredient>() {
            @Override
            public void onSuccess(Ingredient result) {
                loading.postValue(false);
                successMessage.postValue("Ingredient '" + result.getName() + "' updated successfully");
                loadIngredients(); // Reload list
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Delete ingredient (Manager only)
     */
    public void deleteIngredient(String id) {
        loading.setValue(true);
        error.setValue(null);
        successMessage.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.deleteIngredient(token, id, new BaseRepository.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loading.postValue(false);
                successMessage.postValue("Ingredient deleted successfully");
                loadIngredients(); // Reload list
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Add stock transaction
     */
    public void addStockTransaction(String productId, Integer quantity, String transactionType, String notes) {
        loading.setValue(true);
        error.setValue(null);
        successMessage.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        AddStockRequest request = new AddStockRequest(productId, quantity, transactionType, notes);
        
        repository.addStockTransaction(token, request, new BaseRepository.ApiCallback<StockTransaction>() {
            @Override
            public void onSuccess(StockTransaction result) {
                loading.postValue(false);
                String message = transactionType + " transaction completed: " + result.getSignedQuantity() + " units";
                successMessage.postValue(message);
                loadIngredients(); // Reload to update stock levels
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Get transaction history for a product
     */
    public void getTransactionHistory(String productId) {
        loading.setValue(true);
        error.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.getTransactionHistory(token, productId, new BaseRepository.ApiCallback<List<StockTransaction>>() {
            @Override
            public void onSuccess(List<StockTransaction> result) {
                loading.postValue(false);
                transactionHistory.postValue(result);
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Load low stock alerts from server (Manager only)
     */
    public void loadLowStockAlerts() {
        loading.setValue(true);
        error.setValue(null);
        
        String token = getAuthToken();
        if (token == null) {
            loading.postValue(false);
            error.postValue("Authentication required. Please login again.");
            return;
        }
        
        repository.getLowStockAlerts(token, new BaseRepository.ApiCallback<List<LowStockAlert>>() {
            @Override
            public void onSuccess(List<LowStockAlert> result) {
                loading.postValue(false);
                lowStockAlerts.postValue(result);
            }
            
            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }
    
    /**
     * Set stock filter (all, low_stock, out_of_stock)
     */
    public void setStockFilter(String filter) {
        stockFilter.setValue(filter);
        applyFilters();
    }
    
    /**
     * Set search query
     */
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        searchIngredients(query);
    }
    
    /**
     * Apply filters to ingredient list
     */
    private void applyFilters() {
        List<Ingredient> filtered = new ArrayList<>(allIngredients);
        
        String filter = stockFilter.getValue();
        if (filter != null) {
            if (filter.equals("low_stock")) {
                filtered = filtered.stream()
                        .filter(Ingredient::isLowStock)
                        .collect(Collectors.toList());
            } else if (filter.equals("out_of_stock")) {
                filtered = filtered.stream()
                        .filter(Ingredient::isOutOfStock)
                        .collect(Collectors.toList());
            }
        }
        
        ingredientList.postValue(filtered);
    }
    
    /**
     * Clear filters
     */
    public void clearFilters() {
        stockFilter.setValue(null);
        searchQuery.setValue(null);
        applyFilters();
    }
    
    /**
     * Clear error/success messages
     */
    public void clearMessages() {
        error.setValue(null);
        successMessage.setValue(null);
    }
}
