package com.group3.application.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.dto.CashBalanceResponse;
import com.group3.application.model.dto.CashTransactionRequest;
import com.group3.application.model.dto.EndShiftRequest;
import com.group3.application.model.dto.PageResponse;
import com.group3.application.model.dto.StartShiftRequest;
import com.group3.application.model.entity.CashTransaction;
import com.group3.application.model.entity.Shift;
import com.group3.application.model.repository.ShiftRepository;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShiftViewModel extends AndroidViewModel {
    private static final String TAG = "ShiftViewModel";
    private final ShiftRepository shiftRepository;

    private final MutableLiveData<Shift> currentShift = new MutableLiveData<>();
    private final MutableLiveData<CashBalanceResponse> cashBalance = new MutableLiveData<>();
    private final MutableLiveData<List<CashTransaction>> cashTransactions = new MutableLiveData<>();
    private final MutableLiveData<List<Shift>> shiftHistory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public ShiftViewModel(@NonNull Application application) {
        super(application);
        shiftRepository = new ShiftRepository(application);
    }

    // LiveData Getters
    public LiveData<Shift> getCurrentShift() {
        return currentShift;
    }

    public LiveData<CashBalanceResponse> getCashBalance() {
        return cashBalance;
    }

    public LiveData<List<CashTransaction>> getCashTransactions() {
        return cashTransactions;
    }

    public LiveData<List<Shift>> getShiftHistory() {
        return shiftHistory;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    // Start Shift (Check-in)
    public void startShift(Double openingCash) {
        isLoading.setValue(true);
        error.setValue(null);
        
        StartShiftRequest request = new StartShiftRequest(openingCash);
        shiftRepository.startShift(request).enqueue(new Callback<Shift>() {
            @Override
            public void onResponse(@NonNull Call<Shift> call, @NonNull Response<Shift> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentShift.setValue(response.body());
                    successMessage.setValue("Shift started successfully");
                } else {
                    error.setValue("Failed to start shift: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Shift> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error starting shift", t);
            }
        });
    }

    // Get Current Active Shift
    public void fetchCurrentShift() {
        isLoading.setValue(true);
        error.setValue(null);
        
        shiftRepository.getCurrentShift().enqueue(new Callback<Shift>() {
            @Override
            public void onResponse(@NonNull Call<Shift> call, @NonNull Response<Shift> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentShift.setValue(response.body());
                } else if (response.code() == 404) {
                    currentShift.setValue(null);
                    error.setValue("No active shift found");
                } else {
                    error.setValue("Failed to fetch current shift: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Shift> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error fetching current shift", t);
            }
        });
    }

    // End Shift (Check-out)
    public void endShift(Double closingCash) {
        isLoading.setValue(true);
        error.setValue(null);
        
        EndShiftRequest request = new EndShiftRequest(closingCash);
        shiftRepository.endShift(request).enqueue(new Callback<Shift>() {
            @Override
            public void onResponse(@NonNull Call<Shift> call, @NonNull Response<Shift> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentShift.setValue(response.body());
                    successMessage.setValue("Shift ended successfully");
                } else {
                    error.setValue("Failed to end shift: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Shift> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error ending shift", t);
            }
        });
    }

    // Record Cash Transaction
    public void recordCashTransaction(Double amount, String transactionType, 
                                     String description, String referenceNumber) {
        isLoading.setValue(true);
        error.setValue(null);
        
        CashTransactionRequest request = new CashTransactionRequest(amount, transactionType, 
                                                                     description, referenceNumber);
        shiftRepository.recordCashTransaction(request).enqueue(new Callback<CashTransaction>() {
            @Override
            public void onResponse(@NonNull Call<CashTransaction> call, @NonNull Response<CashTransaction> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    successMessage.setValue("Transaction recorded successfully");
                    // Refresh cash balance
                    fetchCashBalance();
                } else {
                    error.setValue("Failed to record transaction: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CashTransaction> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error recording transaction", t);
            }
        });
    }

    // Get Cash Balance
    public void fetchCashBalance() {
        shiftRepository.getCashBalance().enqueue(new Callback<CashBalanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<CashBalanceResponse> call, @NonNull Response<CashBalanceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cashBalance.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch cash balance: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CashBalanceResponse> call, @NonNull Throwable t) {
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error fetching cash balance", t);
            }
        });
    }

    // Get Cash Transactions for Shift
    public void fetchCashTransactions(UUID shiftId) {
        isLoading.setValue(true);
        
        shiftRepository.getCashTransactions(shiftId).enqueue(new Callback<List<CashTransaction>>() {
            @Override
            public void onResponse(@NonNull Call<List<CashTransaction>> call, @NonNull Response<List<CashTransaction>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    cashTransactions.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch transactions: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CashTransaction>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error fetching transactions", t);
            }
        });
    }

    // Get Shift History (Manager only)
    public void fetchShiftHistory(String status, UUID userId, String startDate, 
                                  String endDate, Integer page, Integer size) {
        isLoading.setValue(true);
        error.setValue(null);
        
        shiftRepository.getAllShifts(status, userId, startDate, endDate, page, size)
                .enqueue(new Callback<PageResponse<Shift>>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<Shift>> call, @NonNull Response<PageResponse<Shift>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the content list from the paginated response
                    shiftHistory.setValue(response.body().getContent());
                } else {
                    error.setValue("Failed to fetch shift history: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<Shift>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error fetching shift history", t);
            }
        });
    }

    // Convenience method for fetching all shifts
    public void fetchAllShifts() {
        fetchShiftHistory(null, null, null, null, 0, 100);
    }
}
