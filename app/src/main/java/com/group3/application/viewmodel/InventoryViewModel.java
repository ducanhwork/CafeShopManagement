package com.group3.application.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.dto.IncomingStockRequest;
import com.group3.application.model.dto.IngredientRequest;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.model.entity.LowStockNotification;
import com.group3.application.model.entity.StockTransaction;
import com.group3.application.model.repository.InventoryRepository;

import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryViewModel extends AndroidViewModel {
    private static final String TAG = "InventoryViewModel";
    private final InventoryRepository inventoryRepository;

    private final MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();
    private final MutableLiveData<Ingredient> selectedIngredient = new MutableLiveData<>();
    private final MutableLiveData<List<StockTransaction>> stockTransactions = new MutableLiveData<>();
    private final MutableLiveData<List<LowStockNotification>> lowStockItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        inventoryRepository = new InventoryRepository(application);
    }

    // LiveData Getters
    public LiveData<List<Ingredient>> getIngredients() { return ingredients; }
    public LiveData<Ingredient> getSelectedIngredient() { return selectedIngredient; }
    public LiveData<List<StockTransaction>> getStockTransactions() { return stockTransactions; }
    public LiveData<List<LowStockNotification>> getLowStockItems() { return lowStockItems; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getSuccessMessage() { return successMessage; }

    // Fetch all ingredients
    public void fetchIngredients() {
        isLoading.setValue(true);
        error.setValue(null);
        
        inventoryRepository.listIngredients().enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ingredient>> call, @NonNull Response<List<Ingredient>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ingredients.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch ingredients: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Ingredient>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error fetching ingredients", t);
            }
        });
    }

    // Add new ingredient
    public void addIngredient(String name, String description, Double price, 
                             Integer reorderLevel, String unit, String imageLink, String status) {
        isLoading.setValue(true);
        error.setValue(null);
        
        IngredientRequest request = new IngredientRequest(name, description, price, 
                                                           reorderLevel, unit, imageLink, status);
        inventoryRepository.addIngredient(request).enqueue(new Callback<Ingredient>() {
            @Override
            public void onResponse(@NonNull Call<Ingredient> call, @NonNull Response<Ingredient> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    successMessage.setValue("Ingredient added successfully");
                    fetchIngredients(); // Refresh list
                } else {
                    error.setValue("Failed to add ingredient: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Ingredient> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Update ingredient
    public void updateIngredient(UUID id, String name, String description, Double price,
                                Integer reorderLevel, String unit, String imageLink, String status) {
        isLoading.setValue(true);
        error.setValue(null);
        
        IngredientRequest request = new IngredientRequest(name, description, price,
                                                           reorderLevel, unit, imageLink, status);
        inventoryRepository.updateIngredient(id, request).enqueue(new Callback<Ingredient>() {
            @Override
            public void onResponse(@NonNull Call<Ingredient> call, @NonNull Response<Ingredient> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    successMessage.setValue("Ingredient updated successfully");
                    fetchIngredients();
                } else {
                    error.setValue("Failed to update ingredient: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Ingredient> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Delete ingredient
    public void deleteIngredient(UUID id) {
        isLoading.setValue(true);
        error.setValue(null);
        
        inventoryRepository.deleteIngredient(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    successMessage.setValue("Ingredient deleted successfully");
                    fetchIngredients();
                } else {
                    error.setValue("Failed to delete ingredient: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Add incoming stock
    public void addIncomingStock(UUID productId, Integer quantity, String transactionType, String notes) {
        isLoading.setValue(true);
        error.setValue(null);
        
        IncomingStockRequest request = new IncomingStockRequest(productId, quantity, transactionType, notes);
        inventoryRepository.addIncomingStock(request).enqueue(new Callback<StockTransaction>() {
            @Override
            public void onResponse(@NonNull Call<StockTransaction> call, @NonNull Response<StockTransaction> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    successMessage.setValue("Stock added successfully");
                    fetchIngredients(); // Refresh to show updated quantities
                } else {
                    error.setValue("Failed to add stock: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockTransaction> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Fetch low stock notifications
    public void fetchLowStockNotifications() {
        isLoading.setValue(true);
        error.setValue(null);
        
        inventoryRepository.getLowStockNotifications().enqueue(new Callback<List<LowStockNotification>>() {
            @Override
            public void onResponse(@NonNull Call<List<LowStockNotification>> call, 
                                 @NonNull Response<List<LowStockNotification>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    lowStockItems.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch low stock items: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LowStockNotification>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Fetch transaction history for product
    public void fetchTransactionHistory(UUID productId) {
        isLoading.setValue(true);
        
        inventoryRepository.getTransactionHistory(productId).enqueue(new Callback<List<StockTransaction>>() {
            @Override
            public void onResponse(@NonNull Call<List<StockTransaction>> call,
                                 @NonNull Response<List<StockTransaction>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    stockTransactions.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch transaction history: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StockTransaction>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
