package com.group3.application.model.repository;

import android.content.Context;
import com.group3.application.common.util.HttpException;
import com.group3.application.common.util.Result;
import com.group3.application.model.bean.LoyaltyMemberDetailResponse;
import com.group3.application.model.bean.LoyaltyMemberListItem;
import com.group3.application.model.bean.PointsHistoryItem;
import com.group3.application.model.bean.UpdateLoyaltyMemberRequest;
import com.group3.application.model.dto.CustomerSearchResponse;
import com.group3.application.model.dto.NewCustomerRequest;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Response;

public class LoyaltyRepository {

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    private final ApiService apiService;

    public LoyaltyRepository(Context context) {
        this.apiService = ApiClient.getApi(context.getApplicationContext());
    }

    public Result<List<LoyaltyMemberListItem>> listMembers(String query, String sortBy) {
        try {
            Call<List<LoyaltyMemberListItem>> call = apiService.listLoyaltyMembers(query, sortBy);
            Response<List<LoyaltyMemberListItem>> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return Result.success(response.body());
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                return Result.failure(new HttpException(response.code(), errorBody));
            }
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<LoyaltyMemberDetailResponse> editMember(UUID customerId, UpdateLoyaltyMemberRequest request) {
        try {
            Call<LoyaltyMemberDetailResponse> call = apiService.editLoyaltyMember(customerId, request);
            Response<LoyaltyMemberDetailResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return Result.success(response.body());
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                if (response.code() == 400 || response.code() == 409) {
                    return Result.failure(new HttpException(response.code(), errorBody));
                }
                return Result.failure(new HttpException(response.code(), "API Error"));
            }
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<List<PointsHistoryItem>> getPointsHistory(UUID customerId) {
        try {
            Call<List<PointsHistoryItem>> call = apiService.getPointsHistory(customerId);
            Response<List<PointsHistoryItem>> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return Result.success(response.body());
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                return Result.failure(new HttpException(response.code(), errorBody));
            }
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public void searchCustomerByPhone(String phone, Callback<CustomerSearchResponse> callback) {

        try {
            // Giả định apiService đã có phương thức searchCustomer
            Call<CustomerSearchResponse> call = apiService.searchCustomer(phone);
            Response<CustomerSearchResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                callback.onSuccess(response.body());
            } else if (response.code() == 404) {
                // Khách hàng không tìm thấy (Alternative Flow AT1)
                callback.onError("Customer not found (404)");
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                callback.onError("Lỗi API: " + response.code() + " - " + errorBody);
            }
        } catch (Exception e) {
            callback.onError("Lỗi kết nối mạng: " + e.getMessage());
        }
    }

    public void addNewMember(NewCustomerRequest request, Callback<CustomerSearchResponse> callback) {
        // Chú ý: Đây là thao tác mạng đồng bộ, KHÔNG được gọi trên Main Thread.

        try {
            Call<CustomerSearchResponse> call = apiService.addNewMember(request);
            Response<CustomerSearchResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                callback.onSuccess(response.body());
            } else if (response.code() == 409) { // Giả định 409 Conflict nếu số điện thoại đã tồn tại
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Member already exists";
                callback.onError(errorBody);
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                callback.onError("Lỗi API: " + response.code() + " - " + errorBody);
            }
        } catch (Exception e) {
            callback.onError("Lỗi kết nối mạng: " + e.getMessage());
        }
    }
}
