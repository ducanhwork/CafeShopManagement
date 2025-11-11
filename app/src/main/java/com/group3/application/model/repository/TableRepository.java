package com.group3.application.model.repository;

import android.util.Log;

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

    // --- Phương thức cũ được giữ lại để không làm hỏng code hiện có ---
    public Call<List<TableInfo>> getTables(String status, String keyword) {
        return apiService.listTables(status, keyword);
    }

    // --- Phương thức mới dùng cho màn hình OrderList ---
    public void getAllTables(OnTablesFetchListener listener) {
        // Gọi API để lấy tất cả các bàn (truyền null để không lọc)
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

    public interface OnTablesFetchListener {
        void onTablesFetchComplete(List<TableInfo> tables, String error);
    }
}
