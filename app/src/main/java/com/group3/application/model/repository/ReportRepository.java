package com.group3.application.model.repository;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.PeriodItemReportDTO;
import com.group3.application.model.dto.RevenueReportDTO;
import com.group3.application.model.dto.StockReportDTO;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportRepository {
    private final ApiService apiService;
    private final SharedPreferences sharedPreferences;
    public ReportRepository(Application application) {
        this.apiService = ApiClient.get().create(ApiService.class);
        this.sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private String getAuthToken() {
        String token = sharedPreferences.getString(KEY_AUTH_TOKEN, null);
        return (token == null || token.isEmpty()) ? null : "Bearer " + token;
    }

    public void getRevenueReport(String dateFrom, String dateTo, String filterBy,
                                 RepositoryCallback<APIResult<RevenueReportDTO>> callback) {

        String authToken = getAuthToken();
        if (authToken == null) {
            callback.onComplete(new APIResult<>(false, "Người dùng chưa đăng nhập.", null));
            return;
        }

        apiService.getRevenueReport(authToken, dateFrom, dateTo, filterBy).enqueue(new Callback<RevenueReportDTO>() {
            @Override
            public void onResponse(Call<RevenueReportDTO> call, Response<RevenueReportDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onComplete(new APIResult<>(true, "Success", response.body()));
                } else {
                    callback.onComplete(new APIResult<>(false, "Lỗi server: " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<RevenueReportDTO> call, Throwable t) {
                callback.onComplete(new APIResult<>(false, "Lỗi mạng: " + t.getMessage(), null));
            }
        });
    }

    public void getItemReport(String dateFrom, String dateTo, String filterBy,
                              RepositoryCallback<APIResult<List<PeriodItemReportDTO>>> callback) {

        String authToken = getAuthToken();
        if (authToken == null) {
            callback.onComplete(new APIResult<>(false, "Người dùng chưa đăng nhập.", null));
            return;
        }

        apiService.getItemReport(authToken, dateFrom, dateTo, filterBy).enqueue(new Callback<List<PeriodItemReportDTO>>() {
            @Override
            public void onResponse(Call<List<PeriodItemReportDTO>> call, Response<List<PeriodItemReportDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onComplete(new APIResult<>(true, "Success", response.body()));
                } else {
                    callback.onComplete(new APIResult<>(false, "Lỗi server: " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<List<PeriodItemReportDTO>> call, Throwable t) {
                callback.onComplete(new APIResult<>(false, "Lỗi mạng: " + t.getMessage(), null));
            }
        });
    }

    public void getStockReport(RepositoryCallback<APIResult<StockReportDTO>> callback) {

        String authToken = getAuthToken();
        if (authToken == null) {
            callback.onComplete(new APIResult<>(false, "Người dùng chưa đăng nhập.", null));
            return;
        }

        apiService.getStockReport(authToken).enqueue(new Callback<StockReportDTO>() {
            @Override
            public void onResponse(Call<StockReportDTO> call, Response<StockReportDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onComplete(new APIResult<>(true, "Success", response.body()));
                } else {
                    callback.onComplete(new APIResult<>(false, "Lỗi server: " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<StockReportDTO> call, Throwable t) {
                callback.onComplete(new APIResult<>(false, "Lỗi mạng: " + t.getMessage(), null));
            }
        });
    }
}
