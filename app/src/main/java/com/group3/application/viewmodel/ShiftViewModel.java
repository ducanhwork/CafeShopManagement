package com.group3.application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.EndShiftRequest;
import com.group3.application.model.dto.RecordTransactionRequest;
import com.group3.application.model.dto.StartShiftRequest;
import com.group3.application.model.entity.CashBalance;
import com.group3.application.model.entity.CashTransaction;
import com.group3.application.model.entity.Shift;
import com.group3.application.model.repository.ShiftRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * ViewModel for Shift Management feature.
 * Manages shift state, cash balance, and shift history with LiveData.
 * Follows MVVM pattern with reactive UI updates.
 */
public class ShiftViewModel extends AndroidViewModel {

    private final ShiftRepository shiftRepository;

    // LiveData for current shift state
    private final MutableLiveData<Shift> currentShift = new MutableLiveData<>(null);
    private final MutableLiveData<CashBalance> cashBalance = new MutableLiveData<>(null);
    private final MutableLiveData<List<Shift>> shiftHistory = new MutableLiveData<>(null);
    private final MutableLiveData<CashTransaction> lastTransaction = new MutableLiveData<>(null);

    // LiveData for UI state
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<String> successMessage = new MutableLiveData<>(null);

    // LiveData for shift status
    private final MutableLiveData<Boolean> hasOpenShift = new MutableLiveData<>(false);

    public ShiftViewModel(@NonNull Application application) {
        super(application);
        this.shiftRepository = new ShiftRepository(application);
    }

    // Getters for LiveData (read-only)
    public LiveData<Shift> getCurrentShift() {
        return currentShift;
    }

    public LiveData<CashBalance> getCashBalance() {
        return cashBalance;
    }

    public LiveData<List<Shift>> getShiftHistory() {
        return shiftHistory;
    }

    public LiveData<CashTransaction> getLastTransaction() {
        return lastTransaction;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<Boolean> getHasOpenShift() {
        return hasOpenShift;
    }

    /**
     * Start a new shift with opening cash amount
     */
    public void startShift(BigDecimal openingCash) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        StartShiftRequest request = new StartShiftRequest(openingCash);
        shiftRepository.startShift(request, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                Shift shift = (Shift) result.getData();
                currentShift.postValue(shift);
                hasOpenShift.postValue(true);
                successMessage.postValue(result.getMessage());
                // Load cash balance after starting shift
                loadCashBalance();
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    /**
     * End the current shift with closing cash amount
     */
    public void endShift(BigDecimal closingCash) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        EndShiftRequest request = new EndShiftRequest(closingCash);
        shiftRepository.endShift(request, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                Shift shift = (Shift) result.getData();
                currentShift.postValue(shift);
                hasOpenShift.postValue(false);
                successMessage.postValue(result.getMessage());
                // Clear cash balance after ending shift
                cashBalance.postValue(null);
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    /**
     * Load the current open shift
     */
    public void loadCurrentShift() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        shiftRepository.getCurrentShift(result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                Shift shift = (Shift) result.getData();
                currentShift.postValue(shift);
                hasOpenShift.postValue(shift != null && shift.isOpen());
                
                // Load cash balance if shift is open
                if (shift != null && shift.isOpen()) {
                    loadCashBalance();
                }
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    /**
     * Record a cash transaction (CASH_IN, CASH_OUT, REFUND)
     */
    public void recordTransaction(BigDecimal amount, String transactionType, 
                                  String description, String referenceNumber) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        RecordTransactionRequest request = new RecordTransactionRequest(
                amount, transactionType, description, referenceNumber
        );

        shiftRepository.recordCashTransaction(request, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                CashTransaction transaction = (CashTransaction) result.getData();
                lastTransaction.postValue(transaction);
                successMessage.postValue(result.getMessage());
                // Reload cash balance after recording transaction
                loadCashBalance();
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    /**
     * Load current cash balance for the active shift
     */
    public void loadCashBalance() {
        shiftRepository.getCashBalance(result -> {
            if (result.isSuccess()) {
                CashBalance balance = (CashBalance) result.getData();
                cashBalance.postValue(balance);
            } else {
                // Don't show error for cash balance load failures
                // Just keep the old balance or null
            }
        });
    }

    /**
     * Load shift history with pagination
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param status Filter by status (OPEN, CLOSED, or null for all)
     */
    public void loadShiftHistory(int page, int size, String status) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        shiftRepository.getShiftHistory(page, size, status, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Shift> shifts = (List<Shift>) result.getData();
                shiftHistory.postValue(shifts);
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    /**
     * Clear error message (call after showing to user)
     */
    public void clearError() {
        errorMessage.setValue(null);
    }

    /**
     * Clear success message (call after showing to user)
     */
    public void clearSuccess() {
        successMessage.setValue(null);
    }

    /**
     * Refresh all shift data
     */
    public void refreshData() {
        loadCurrentShift();
    }
}
