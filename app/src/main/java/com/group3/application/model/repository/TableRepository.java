package com.group3.application.model.repository;

import android.util.Log;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableRepository {
    private static final String TAG = "TableRepository";
    private final ApiService apiService;

    public TableRepository() {
        this.apiService = ApiClient.get().create(ApiService.class);
    }

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
}
