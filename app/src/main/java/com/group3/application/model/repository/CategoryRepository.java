package com.group3.application.model.repository;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.entity.Category;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;
import com.group3.application.viewmodel.LoginViewModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private static final String TAG = "CategoryRepository";
    private final SharedPreferences prefs;
    private final ApiService apiService;
    private final Application application;

    public CategoryRepository() {
        this.apiService = ApiClient.get().create(ApiService.class);
        this.application = null;
        this.prefs = null;
    }

    public CategoryRepository(Application application) {
        this.application = application;
        this.apiService = ApiClient.get().create(ApiService.class);
        this.prefs = application.getSharedPreferences(LoginViewModel.PREF_NAME, Context.MODE_PRIVATE);
    }

    public Call<List<Category>> getCategories() {
        return apiService.getCategories();
    }

    public void getCategories(OnGetCategoriesListener listener) {
        apiService.listCategories("Bearer " + prefs.getString(KEY_AUTH_TOKEN, null)).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() & response.body() != null) {
                    List<Category> categories = response.body();
                    listener.onGetCategoriesComplete(new APIResult(true, "Get categories successfully", categories));
                } else {
                    try {
                        String error = response.errorBody().string();
                        Gson gson = new Gson();
                        APIResult apiResult = gson.fromJson(error, APIResult.class);
                        listener.onGetCategoriesComplete(apiResult);
                        Log.e(TAG, "onResponse: " + apiResult.getMessage());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable throwable) {
                Log.e(TAG, "onFailure: " + throwable.getMessage());
                listener.onGetCategoriesComplete(new APIResult(false, "Get categories failed", null));
            }
        });
    }

    public interface OnGetCategoriesListener {
        void onGetCategoriesComplete(APIResult result);
    }
}
