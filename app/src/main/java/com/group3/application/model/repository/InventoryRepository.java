package com.group3.application.model.repository;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.model.bean.AddStockRequest;
import com.group3.application.model.bean.CreateIngredientRequest;
import com.group3.application.model.bean.UpdateIngredientRequest;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.model.entity.LowStockAlert;
import com.group3.application.model.entity.StockTransaction;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.InventoryApiService;

import java.util.List;

import retrofit2.Call;

/**
 * Repository for Inventory Management operations
 * Handles all API calls related to ingredients, stock, and transactions
 */
public class InventoryRepository extends BaseRepository {
    
    private final InventoryApiService apiService;
    
    public InventoryRepository() {
        this.apiService = ApiClient.get().create(InventoryApiService.class);
    }
    
    /**
     * Get all active ingredients
     */
    public void getAllIngredients(String token, ApiCallback<List<Ingredient>> callback) {
        Call<List<Ingredient>> call = apiService.getAllIngredients(token);
        executeCall(call, callback);
    }
    
    /**
     * Get a single ingredient by ID
     */
    public void getIngredientById(String token, String id, ApiCallback<Ingredient> callback) {
        Call<Ingredient> call = apiService.getIngredientById(token, id);
        executeCall(call, callback);
    }
    
    /**
     * Search ingredients by name
     */
    public void searchIngredients(String token, String name, ApiCallback<List<Ingredient>> callback) {
        Call<List<Ingredient>> call = apiService.searchIngredients(token, name);
        executeCall(call, callback);
    }
    
    /**
     * Create a new ingredient (Manager only)
     */
    public void createIngredient(String token, CreateIngredientRequest request, ApiCallback<Ingredient> callback) {
        Call<Ingredient> call = apiService.createIngredient(token, request);
        executeCall(call, callback);
    }
    
    /**
     * Update an existing ingredient (Manager only)
     */
    public void updateIngredient(String token, String id, UpdateIngredientRequest request, ApiCallback<Ingredient> callback) {
        Call<Ingredient> call = apiService.updateIngredient(token, id, request);
        executeCall(call, callback);
    }
    
    /**
     * Delete an ingredient (Manager only)
     */
    public void deleteIngredient(String token, String id, ApiCallback<Void> callback) {
        Call<Void> call = apiService.deleteIngredient(token, id);
        executeCall(call, callback);
    }
    
    /**
     * Add stock transaction (INCOMING, OUTGOING, ADJUSTMENT)
     */
    public void addStockTransaction(String token, AddStockRequest request, ApiCallback<StockTransaction> callback) {
        Call<StockTransaction> call = apiService.addStockTransaction(token, request);
        executeCall(call, callback);
    }
    
    /**
     * Get current stock level for a product
     */
    public void getStockLevel(String token, String productId, ApiCallback<Ingredient> callback) {
        Call<Ingredient> call = apiService.getStockLevel(token, productId);
        executeCall(call, callback);
    }
    
    /**
     * Get transaction history for a product
     */
    public void getTransactionHistory(String token, String productId, ApiCallback<List<StockTransaction>> callback) {
        Call<List<StockTransaction>> call = apiService.getTransactionHistory(token, productId);
        executeCall(call, callback);
    }
    
    /**
     * Get ingredients with low stock (Manager only)
     */
    public void getLowStockAlerts(String token, ApiCallback<List<LowStockAlert>> callback) {
        Call<List<LowStockAlert>> call = apiService.getLowStockAlerts(token);
        executeCall(call, callback);
    }
}
