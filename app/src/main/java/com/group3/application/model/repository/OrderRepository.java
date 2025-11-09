package com.group3.application.model.repository;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.OrderItemDTO;
import com.group3.application.model.dto.OrderRequest;
import com.group3.application.model.entity.Order;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static final String TAG = "OrderRepository";
    private final ApiService apiService;
    private final SharedPreferences sharedPreferences;

    public OrderRepository(Application application) {
        this.apiService = ApiClient.get().create(ApiService.class);
        this.sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public interface OnResultListener<T> {
        void onResult(APIResult<T> result);
    }

    // SỬA: Sửa lại logic để khớp với kiểu trả về của server
    public void getOrderDetails(String orderId, OnResultListener<Order> listener) {
        String authToken = getAuthToken();
        if (authToken == null) {
            listener.onResult(new APIResult<>(false, "Người dùng chưa đăng nhập.", null));
            return;
        }
        apiService.getOrderDetails(authToken, orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onResult(new APIResult<>(true, "Success", response.body()));
                } else {
                    String errorMsg = "Lỗi lấy chi tiết đơn hàng. Mã: " + response.code();
                    Log.e(TAG, errorMsg);
                    listener.onResult(new APIResult<>(false, errorMsg, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Log.e(TAG, "Network Failure: " + t.getMessage(), t);
                listener.onResult(new APIResult<>(false, "Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void getOrders(String status, String tableId, String staffId, OnResultListener<List<Order>> listener) {
        String authToken = getAuthToken();
        if (authToken == null) {
            listener.onResult(new APIResult<>(false, "Người dùng chưa đăng nhập.", null));
            return;
        }
        apiService.getOrders(authToken, status, tableId, staffId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onResult(new APIResult<>(true, "Success", response.body()));
                } else {
                    String errorMsg = "Lỗi lấy danh sách đơn hàng. Mã: " + response.code();
                    Log.e(TAG, errorMsg);
                    listener.onResult(new APIResult<>(false, errorMsg, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network Failure: " + t.getMessage(), t);
                listener.onResult(new APIResult<>(false, "Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void createOrder(List<String> tableIds, List<OrderItemDTO> orderItems, String note, OnResultListener<Object> listener) {
        String authToken = getAuthToken();
        if (authToken == null) {
            listener.onResult(new APIResult<>(false, "Người dùng chưa đăng nhập.", null));
            return;
        }
        OrderRequest orderRequest = new OrderRequest(tableIds, orderItems, note);
        apiService.createOrder(authToken, orderRequest).enqueue(new DefaultCallback<>(listener));
    }

    public void updateOrderItems(String orderId, List<OrderItemDTO> items, String note, OnResultListener<Object> listener) {
        String authToken = getAuthToken();
        if (authToken == null) {
            listener.onResult(new APIResult<>(false, "Người dùng chưa đăng nhập.", null));
            return;
        }
        OrderRequest updateRequest = new OrderRequest(null, items, note);
        apiService.updateOrderItems(authToken, orderId, updateRequest).enqueue(new DefaultCallback<>(listener));
    }

    private String getAuthToken() {
        String token = sharedPreferences.getString(KEY_AUTH_TOKEN, null);
        return (token == null || token.isEmpty()) ? null : "Bearer " + token;
    }

    private static class DefaultCallback<T> implements Callback<APIResult<T>> {
        private final OnResultListener<T> listener;

        DefaultCallback(OnResultListener<T> listener) {
            this.listener = listener;
        }

        @Override
        public void onResponse(@NonNull Call<APIResult<T>> call, @NonNull Response<APIResult<T>> response) {
            if (response.isSuccessful() && response.body() != null) {
                listener.onResult(response.body());
            } else {
                String errorMsg = "API Error code: " + response.code();
                Log.e(TAG, errorMsg);
                listener.onResult(new APIResult<>(false, errorMsg, null));
            }
        }

        @Override
        public void onFailure(@NonNull Call<APIResult<T>> call, @NonNull Throwable t) {
            Log.e(TAG, "Network Failure: " + t.getMessage(), t);
            listener.onResult(new APIResult<>(false, "Lỗi kết nối: " + t.getMessage(), null));
        }
    }
}
