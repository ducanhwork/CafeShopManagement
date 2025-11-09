package com.group3.application.model.webservice;

import com.group3.application.model.bean.AddStockRequest;
import com.group3.application.model.bean.CreateIngredientRequest;
import com.group3.application.model.bean.UpdateIngredientRequest;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.model.entity.LowStockAlert;
import com.group3.application.model.entity.StockTransaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API service for Inventory Management
 * Base URL: http://10.0.2.2:8080/api
 * All endpoints require JWT Bearer token authorization
 */
public interface InventoryApiService {
    
    /**
     * Get all active ingredients
     * GET /inventory/ingredients
     */
    @GET("inventory/ingredients")
    Call<List<Ingredient>> getAllIngredients(
            @Header("Authorization") String authorization
    );
    
    /**
     * Get a single ingredient by ID
     * GET /inventory/ingredients/{id}
     */
    @GET("inventory/ingredients/{id}")
    Call<Ingredient> getIngredientById(
            @Header("Authorization") String authorization,
            @Path("id") String id
    );
    
    /**
     * Search ingredients by name
     * GET /inventory/ingredients/search?name={name}
     */
    @GET("inventory/ingredients/search")
    Call<List<Ingredient>> searchIngredients(
            @Header("Authorization") String authorization,
            @Query("name") String name
    );
    
    /**
     * Create a new ingredient (Manager only)
     * POST /inventory/ingredients
     */
    @POST("inventory/ingredients")
    Call<Ingredient> createIngredient(
            @Header("Authorization") String authorization,
            @Body CreateIngredientRequest request
    );
    
    /**
     * Update an existing ingredient (Manager only)
     * PUT /inventory/ingredients/{id}
     */
    @PUT("inventory/ingredients/{id}")
    Call<Ingredient> updateIngredient(
            @Header("Authorization") String authorization,
            @Path("id") String id,
            @Body UpdateIngredientRequest request
    );
    
    /**
     * Delete an ingredient - soft delete (Manager only)
     * DELETE /inventory/ingredients/{id}
     */
    @DELETE("inventory/ingredients/{id}")
    Call<Void> deleteIngredient(
            @Header("Authorization") String authorization,
            @Path("id") String id
    );
    
    /**
     * Add stock transaction (INCOMING, OUTGOING, ADJUSTMENT)
     * POST /inventory/stock/incoming
     */
    @POST("inventory/stock/incoming")
    Call<StockTransaction> addStockTransaction(
            @Header("Authorization") String authorization,
            @Body AddStockRequest request
    );
    
    /**
     * Get current stock level for a product
     * GET /inventory/stock/{productId}
     */
    @GET("inventory/stock/{productId}")
    Call<Ingredient> getStockLevel(
            @Header("Authorization") String authorization,
            @Path("productId") String productId
    );
    
    /**
     * Get transaction history for a product
     * GET /inventory/transactions/{productId}
     */
    @GET("inventory/transactions/{productId}")
    Call<List<StockTransaction>> getTransactionHistory(
            @Header("Authorization") String authorization,
            @Path("productId") String productId
    );
    
    /**
     * Get ingredients with low stock (Manager only)
     * GET /inventory/low-stock
     */
    @GET("inventory/low-stock")
    Call<List<LowStockAlert>> getLowStockAlerts(
            @Header("Authorization") String authorization
    );
}
