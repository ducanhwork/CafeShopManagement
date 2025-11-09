package com.group3.application.model.repository;

import android.content.Context;
import com.group3.application.common.util.HttpException;
import com.group3.application.common.util.Result;
import com.group3.application.model.bean.LoyaltyMemberDetailResponse;
import com.group3.application.model.bean.LoyaltyMemberListItem;
import com.group3.application.model.bean.PointsHistoryItem;
import com.group3.application.model.bean.UpdateLoyaltyMemberRequest;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Response;

public class LoyaltyRepository {

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
}
