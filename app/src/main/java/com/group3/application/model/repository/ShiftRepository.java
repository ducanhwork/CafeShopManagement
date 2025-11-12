package com.group3.application.model.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.group3.application.model.dto.CashBalanceResponse;
import com.group3.application.model.dto.CashTransactionRequest;
import com.group3.application.model.dto.EndShiftRequest;
import com.group3.application.model.dto.PageResponse;
import com.group3.application.model.dto.StartShiftRequest;
import com.group3.application.model.entity.CashTransaction;
import com.group3.application.model.entity.Shift;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

public class ShiftRepository {
    private final ApiService apiService;
    private final SharedPreferences prefs;

    public ShiftRepository(Application application) {
        apiService = ApiClient.get().create(ApiService.class);
        prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private String getAuthToken() {
        return "Bearer " + prefs.getString(KEY_AUTH_TOKEN, "");
    }

    public Call<Shift> startShift(StartShiftRequest request) {
        return apiService.startShift(getAuthToken(), request);
    }

    public Call<Shift> getCurrentShift() {
        return apiService.getCurrentShift(getAuthToken());
    }

    public Call<Shift> endShift(EndShiftRequest request) {
        return apiService.endShift(getAuthToken(), request);
    }

    public Call<CashTransaction> recordCashTransaction(CashTransactionRequest request) {
        return apiService.recordCashTransaction(getAuthToken(), request);
    }

    public Call<CashBalanceResponse> getCashBalance() {
        return apiService.getCashBalance(getAuthToken());
    }

    public Call<List<CashTransaction>> getCashTransactions(UUID shiftId) {
        return apiService.getCashTransactions(getAuthToken(), shiftId);
    }

    public Call<PageResponse<Shift>> getAllShifts(String status, UUID userId, String startDate, 
                                          String endDate, Integer page, Integer size) {
        return apiService.getAllShifts(getAuthToken(), status, userId, startDate, endDate, page, size);
    }
}
