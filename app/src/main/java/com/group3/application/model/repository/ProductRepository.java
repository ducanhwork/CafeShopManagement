package com.group3.application.model.repository;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.net.ParseException;

import com.google.gson.Gson;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.entity.Product;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;
import com.group3.application.viewmodel.LoginViewModel;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static final String TAG = "ProductRepository";
    private final ApiService apiService;
    private final Application application;

    public ProductRepository(Application application) {
        this.application = application;
        this.apiService = ApiClient.get().create(ApiService.class);
    }

    public void getProducts(OnGetProductsListener listener, String keyword, String category) {
        SharedPreferences prefs = application.getSharedPreferences(LoginViewModel.PREF_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_AUTH_TOKEN, null);
        apiService.listProducts("Bearer "+token, keyword, category).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() & response.body() != null) {
                    List<Product> products = response.body();
                    listener.onGetProductsComplete(new APIResult(true, "Get products successfully", products));
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Gson gson = new Gson();
                        APIResult apiResult = gson.fromJson(errorResponse, APIResult.class);
                        Log.e(TAG, "onResponse: " + apiResult.getMessage());
                        listener.onGetProductsComplete(apiResult);
                    } catch (IOException e) {
                        Log.e(TAG, "onResponse: error pares");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "onFailure get products: " + t.getMessage());
                listener.onGetProductsComplete(new APIResult(false, "onFailure get products: " + t.getMessage()));
            }
        });
    }

    public void updateProductStatus(UUID productId, Boolean status, OnUpdateProductStatusListener listener) {
        SharedPreferences prefs = application.getSharedPreferences(LoginViewModel.PREF_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_AUTH_TOKEN, null);
        apiService.updateProductStatus("Bearer "+token, productId, status).enqueue(new Callback<APIResult>() {
            @Override
            public void onResponse(Call<APIResult> call, Response<APIResult> response) {
                if (response.isSuccessful() & response.body() != null) {
                    APIResult result = response.body();
                    listener.onUpdateProductStatusComplete(result);
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Gson gson = new Gson();
                        APIResult apiResult = gson.fromJson(errorResponse, APIResult.class);
                        Log.e(TAG, "onResponse: " + apiResult.getMessage());
                        listener.onUpdateProductStatusComplete(apiResult);
                    } catch (IOException e) {
                        Log.e(TAG, "onResponse: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResult> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                listener.onUpdateProductStatusComplete(new APIResult(false, t.getMessage()));
            }
        });
    }

    public interface OnUpdateProductStatusListener {
        void onUpdateProductStatusComplete(APIResult result);
    }

    public interface OnGetProductsListener {
        void onGetProductsComplete(APIResult result);
    }
}
