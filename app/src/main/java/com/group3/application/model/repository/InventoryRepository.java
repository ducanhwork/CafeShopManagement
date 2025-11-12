package com.group3.application.model.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.group3.application.model.dto.IncomingStockRequest;
import com.group3.application.model.dto.IngredientRequest;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.model.entity.LowStockNotification;
import com.group3.application.model.entity.StockTransaction;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

public class InventoryRepository {
    private final ApiService apiService;
    private final SharedPreferences prefs;

    public InventoryRepository(Application application) {
        apiService = ApiClient.get().create(ApiService.class);
        prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private String getAuthToken() {
        return "Bearer " + prefs.getString(KEY_AUTH_TOKEN, "");
    }

    public Call<Ingredient> addIngredient(IngredientRequest request) {
        return apiService.addIngredient(getAuthToken(), request);
    }

    public Call<Ingredient> updateIngredient(UUID id, IngredientRequest request) {
        return apiService.updateIngredient(getAuthToken(), id, request);
    }

    public Call<Void> deleteIngredient(UUID id) {
        return apiService.deleteIngredient(getAuthToken(), id);
    }

    public Call<Ingredient> getIngredient(UUID id) {
        return apiService.getIngredient(getAuthToken(), id);
    }

    public Call<List<Ingredient>> listIngredients() {
        return apiService.listIngredients(getAuthToken());
    }

    public Call<List<Ingredient>> searchIngredients(String name) {
        return apiService.searchIngredients(getAuthToken(), name);
    }

    public Call<StockTransaction> addIncomingStock(IncomingStockRequest request) {
        return apiService.addIncomingStock(getAuthToken(), request);
    }

    public Call<List<StockTransaction>> getTransactionHistory(UUID productId) {
        return apiService.getTransactionHistory(getAuthToken(), productId);
    }

    public Call<List<LowStockNotification>> getLowStockNotifications() {
        return apiService.getLowStockNotifications(getAuthToken());
    }
}
