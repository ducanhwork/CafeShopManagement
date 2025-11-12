package com.group3.application.model.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.group3.application.model.dto.CreateTableRequest;
import com.group3.application.model.dto.PageResponse;
import com.group3.application.model.dto.UpdateTableRequest;
import android.util.Log;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableRepository {
    private static final String TAG = "TableRepository";
    private final ApiService apiService;
    private SharedPreferences sharedPreferences;

    public TableRepository(Context context) {
        this.apiService = ApiClient.get().create(ApiService.class);
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private String getAuthToken() {
        if (sharedPreferences != null) {
            String token = sharedPreferences.getString(KEY_AUTH_TOKEN, "");
            if (!token.isEmpty()) {
                return token.startsWith("Bearer ") ? token : "Bearer " + token;
            }
        }
        return "";
    }

    // List tables with filters    }

    public Call<List<TableInfo>> getTables(String status, String keyword) {
        return apiService.listTables(status, keyword);
    }

    public void getAllTables(OnTablesFetchListener listener) {
        apiService.listTables(null, null).enqueue(new Callback<List<TableInfo>>() {
            @Override
            public void onResponse(Call<List<TableInfo>> call, Response<List<TableInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onTablesFetchComplete(response.body(), null);
                } else {
                    String error = "Lỗi lấy danh sách bàn. Mã: " + response.code();
                    Log.e(TAG, error);
                    listener.onTablesFetchComplete(null, error);
                }
            }

            @Override
            public void onFailure(Call<List<TableInfo>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                listener.onTablesFetchComplete(null, "Lỗi kết nối mạng.");
            }
        });
    }

    public void updateTableStatus(String authToken, String tableId, String status, final RepositoryCallback<APIResult> callback) {
        apiService.updateTableStatus(authToken, tableId, status).enqueue(new Callback<APIResult>() {
            @Override
            public void onResponse(Call<APIResult> call, Response<APIResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onComplete(new APIResult(true, response.body().getMessage(), null));
                } else {
                    String errorMsg = "Cập nhật trạng thái bàn thất bại. Code: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onComplete(new APIResult(false, errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<APIResult> call, Throwable t) {
                Log.e(TAG, "Network error while updating table status: " + t.getMessage(), t);
                callback.onComplete(new APIResult(false, t.getMessage(), null));
            }
        });
    }

    public interface OnTablesFetchListener {
        void onTablesFetchComplete(List<TableInfo> tables, String error);
    }

    // Get all tables with pagination (for manager)
    public Call<PageResponse<TableInfo>> getAllTablesWithPagination(String status, String location, 
                                                             Integer minSeatCount, Integer page, 
                                                             Integer size, String sort) {
        return apiService.getAllTablesWithPagination(getAuthToken(), status, location, minSeatCount, page, size, sort);
    }

    // Get table by ID
    public Call<TableInfo> getTableById(String id) {
        return apiService.getTableById(getAuthToken(), id);
    }

    // Create new table
    public Call<TableInfo> createTable(CreateTableRequest request) {
        return apiService.createTable(getAuthToken(), request);
    }

    // Update table
    public Call<TableInfo> updateTable(String id, UpdateTableRequest request) {
        return apiService.updateTable(getAuthToken(), id, request);
    }

    // Update table status
    public Call<TableInfo> updateTableStatus(String id, Map<String, String> status) {
        return apiService.updateTableStatus(getAuthToken(), id, status);
    }

    // Delete table
    public Call<Map<String, String>> deleteTable(String id) {
        return apiService.deleteTable(getAuthToken(), id);
    }
}
