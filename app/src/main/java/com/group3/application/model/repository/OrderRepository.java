package com.group3.application.model.repository;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

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

    public void createOrder(List<String> tableIds, List<OrderItemDTO> orderItems, String note, OnOrderCreateListener listener) {
        String authToken = sharedPreferences.getString(KEY_AUTH_TOKEN, null);
        if (authToken == null || authToken.isEmpty()) {
            listener.onOrderCreateComplete(new APIResult(false, "Lỗi: Người dùng chưa đăng nhập.", null));
            return;
        }
        String finalAuthToken = "Bearer " + authToken;
        OrderRequest orderRequest = new OrderRequest(tableIds, orderItems, note);

        apiService.createOrder(finalAuthToken, orderRequest).enqueue(new Callback<APIResult>() {
            @Override
            public void onResponse(Call<APIResult> call, Response<APIResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onOrderCreateComplete(response.body());
                } else {
                    String errorMessage = "Tạo đơn hàng thất bại. Mã lỗi: " + response.code();
                    Log.e(TAG, "API Error: " + errorMessage);
                    listener.onOrderCreateComplete(new APIResult(false, errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<APIResult> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage(), t);
                listener.onOrderCreateComplete(new APIResult(false, "Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void getOrders(@Nullable String status, @Nullable String tableId, @Nullable String staffId, OnOrdersFetchListener listener) {
        String authToken = sharedPreferences.getString(KEY_AUTH_TOKEN, null);
        if (authToken == null || authToken.isEmpty()) {
            listener.onOrdersFetchComplete(null, "Lỗi: Người dùng chưa đăng nhập.");
            return;
        }
        String finalAuthToken = "Bearer " + authToken;

        apiService.getOrders(finalAuthToken, status, tableId, staffId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onOrdersFetchComplete(response.body(), null);
                } else {
                    String error = "Lỗi lấy danh sách đơn hàng. Mã: " + response.code();
                    Log.e(TAG, error);
                    listener.onOrdersFetchComplete(null, error);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                listener.onOrdersFetchComplete(null, "Lỗi kết nối mạng.");
            }
        });
    }

    // SỬA: Thêm phương thức lấy chi tiết Order
    public void getOrderById(String orderId, OnOrderDetailFetchListener listener) {
        String authToken = sharedPreferences.getString(KEY_AUTH_TOKEN, null);
        if (authToken == null || authToken.isEmpty()) {
            listener.onOrderDetailFetchComplete(null, "Lỗi: Người dùng chưa đăng nhập.");
            return;
        }
        String finalAuthToken = "Bearer " + authToken;

        apiService.getOrderById(finalAuthToken, orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onOrderDetailFetchComplete(response.body(), null);
                } else {
                    String error = "Lỗi lấy chi tiết đơn hàng. Mã: " + response.code();
                    Log.e(TAG, error);
                    listener.onOrderDetailFetchComplete(null, error);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                listener.onOrderDetailFetchComplete(null, "Lỗi kết nối mạng.");
            }
        });
    }


    public interface OnOrderCreateListener {
        void onOrderCreateComplete(APIResult result);
    }

    public interface OnOrdersFetchListener {
        void onOrdersFetchComplete(List<Order> orders, String error);
    }

    // SỬA: Thêm interface cho việc lấy chi tiết
    public interface OnOrderDetailFetchListener {
        void onOrderDetailFetchComplete(Order order, String error);
    }
}
