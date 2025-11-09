package com.group3.application.model.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.EndShiftRequest;
import com.group3.application.model.dto.RecordTransactionRequest;
import com.group3.application.model.dto.StartShiftRequest;
import com.group3.application.model.entity.CashBalance;
import com.group3.application.model.entity.CashTransaction;
import com.group3.application.model.entity.Shift;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ShiftApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.group3.application.viewmodel.LoginViewModel.KEY_AUTH_TOKEN;
import static com.group3.application.viewmodel.LoginViewModel.PREF_NAME;

/**
 * Repository for Shift Management operations.
 * Handles all shift-related API calls with callback pattern.
 * Follows the same pattern as AuthRepository for consistency.
 */
public class ShiftRepository {
    private static final String TAG = "ShiftRepository";
    private final ShiftApiService shiftApiService;
    private final Application application;

    public ShiftRepository(Application application) {
        this.application = application;
        this.shiftApiService = ApiClient.get().create(ShiftApiService.class);
    }

    /**
     * Start a new shift with opening cash amount
     * POST /shifts/start
     */
    public void startShift(StartShiftRequest request, OnStartShiftListener listener) {
        String token = getAuthToken();
        if (token == null) {
            listener.onStartShiftComplete(new APIResult(false, "Vui lòng đăng nhập lại", null));
            return;
        }

        shiftApiService.startShift("Bearer " + token, request).enqueue(new Callback<Shift>() {
            @Override
            public void onResponse(@NonNull Call<Shift> call, @NonNull Response<Shift> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Shift shift = response.body();
                    Log.d(TAG, "Shift started successfully: " + shift.getId());
                    listener.onStartShiftComplete(new APIResult(
                            true,
                            "Ca làm việc đã được bắt đầu",
                            shift
                    ));
                } else {
                    String errorMessage = handleErrorResponse(response, "Không thể bắt đầu ca làm việc");
                    Log.e(TAG, "Start shift failed: " + errorMessage);
                    listener.onStartShiftComplete(new APIResult(false, errorMessage, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Shift> call, @NonNull Throwable t) {
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Start shift network error: " + errorMessage, t);
                listener.onStartShiftComplete(new APIResult(false, errorMessage, null));
            }
        });
    }

    /**
     * End the current shift with closing cash amount
     * POST /shifts/end
     */
    public void endShift(EndShiftRequest request, OnEndShiftListener listener) {
        String token = getAuthToken();
        if (token == null) {
            listener.onEndShiftComplete(new APIResult(false, "Vui lòng đăng nhập lại", null));
            return;
        }

        shiftApiService.endShift("Bearer " + token, request).enqueue(new Callback<Shift>() {
            @Override
            public void onResponse(@NonNull Call<Shift> call, @NonNull Response<Shift> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Shift shift = response.body();
                    Log.d(TAG, "Shift ended successfully: " + shift.getId());
                    listener.onEndShiftComplete(new APIResult(
                            true,
                            "Ca làm việc đã kết thúc",
                            shift
                    ));
                } else {
                    String errorMessage = handleErrorResponse(response, "Không thể kết thúc ca làm việc");
                    Log.e(TAG, "End shift failed: " + errorMessage);
                    listener.onEndShiftComplete(new APIResult(false, errorMessage, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Shift> call, @NonNull Throwable t) {
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "End shift network error: " + errorMessage, t);
                listener.onEndShiftComplete(new APIResult(false, errorMessage, null));
            }
        });
    }

    /**
     * Get the current open shift for the logged-in user
     * GET /shifts/current
     */
    public void getCurrentShift(OnGetCurrentShiftListener listener) {
        String token = getAuthToken();
        if (token == null) {
            listener.onGetCurrentShiftComplete(new APIResult(false, "Vui lòng đăng nhập lại", null));
            return;
        }

        shiftApiService.getCurrentShift("Bearer " + token).enqueue(new Callback<Shift>() {
            @Override
            public void onResponse(@NonNull Call<Shift> call, @NonNull Response<Shift> response) {
                if (response.isSuccessful()) {
                    Shift shift = response.body(); // May be null if no shift is open
                    if (shift != null) {
                        Log.d(TAG, "Current shift retrieved: " + shift.getId());
                        listener.onGetCurrentShiftComplete(new APIResult(
                                true,
                                "Lấy thông tin ca làm việc thành công",
                                shift
                        ));
                    } else {
                        Log.d(TAG, "No current shift found");
                        listener.onGetCurrentShiftComplete(new APIResult(
                                true,
                                "Không có ca làm việc đang mở",
                                null
                        ));
                    }
                } else {
                    // Check if error is "no active shift found" which is actually a valid state
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }

                    // Handle "No active shift found" as a successful response with null data
                    if (errorBody.contains("No active shift found") || 
                        errorBody.contains("Please start a shift first")) {
                        Log.d(TAG, "No active shift - this is a valid state");
                        listener.onGetCurrentShiftComplete(new APIResult(
                                true,
                                "Không có ca làm việc đang mở",
                                null
                        ));
                    } else {
                        String errorMessage = "Không thể lấy thông tin ca làm việc. Mã lỗi: " + 
                                            response.code() + 
                                            (errorBody.isEmpty() ? "" : " - " + errorBody);
                        Log.e(TAG, "Get current shift failed: " + errorMessage);
                        listener.onGetCurrentShiftComplete(new APIResult(false, errorMessage, null));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Shift> call, @NonNull Throwable t) {
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Get current shift network error: " + errorMessage, t);
                listener.onGetCurrentShiftComplete(new APIResult(false, errorMessage, null));
            }
        });
    }

    /**
     * Record a cash transaction (IN/OUT/REFUND)
     * POST /shifts/cash/record
     */
    public void recordCashTransaction(RecordTransactionRequest request, OnRecordTransactionListener listener) {
        String token = getAuthToken();
        if (token == null) {
            listener.onRecordTransactionComplete(new APIResult(false, "Vui lòng đăng nhập lại", null));
            return;
        }

        shiftApiService.recordCashTransaction("Bearer " + token, request).enqueue(new Callback<CashTransaction>() {
            @Override
            public void onResponse(@NonNull Call<CashTransaction> call, @NonNull Response<CashTransaction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CashTransaction transaction = response.body();
                    Log.d(TAG, "Transaction recorded: " + transaction.getId());
                    listener.onRecordTransactionComplete(new APIResult(
                            true,
                            "Ghi nhận giao dịch thành công",
                            transaction
                    ));
                } else {
                    String errorMessage = handleErrorResponse(response, "Không thể ghi nhận giao dịch");
                    Log.e(TAG, "Record transaction failed: " + errorMessage);
                    listener.onRecordTransactionComplete(new APIResult(false, errorMessage, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CashTransaction> call, @NonNull Throwable t) {
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Record transaction network error: " + errorMessage, t);
                listener.onRecordTransactionComplete(new APIResult(false, errorMessage, null));
            }
        });
    }

    /**
     * Get current cash balance for the active shift
     * GET /shifts/cash/balance
     */
    public void getCashBalance(OnGetCashBalanceListener listener) {
        String token = getAuthToken();
        if (token == null) {
            listener.onGetCashBalanceComplete(new APIResult(false, "Vui lòng đăng nhập lại", null));
            return;
        }

        shiftApiService.getCashBalance("Bearer " + token).enqueue(new Callback<CashBalance>() {
            @Override
            public void onResponse(@NonNull Call<CashBalance> call, @NonNull Response<CashBalance> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CashBalance balance = response.body();
                    Log.d(TAG, "Cash balance retrieved - Expected Balance: " + balance.getExpectedBalance());
                    listener.onGetCashBalanceComplete(new APIResult(
                            true,
                            "Lấy số dư tiền mặt thành công",
                            balance
                    ));
                } else {
                    String errorMessage = handleErrorResponse(response, "Không thể lấy số dư tiền mặt");
                    Log.e(TAG, "Get cash balance failed: " + errorMessage);
                    listener.onGetCashBalanceComplete(new APIResult(false, errorMessage, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CashBalance> call, @NonNull Throwable t) {
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Get cash balance network error: " + errorMessage, t);
                listener.onGetCashBalanceComplete(new APIResult(false, errorMessage, null));
            }
        });
    }

    /**
     * Get shift history with pagination and optional status filter
     * GET /shifts?page=0&size=20&status=CLOSED
     */
    public void getShiftHistory(int page, int size, String status, OnGetShiftHistoryListener listener) {
        String token = getAuthToken();
        if (token == null) {
            listener.onGetShiftHistoryComplete(new APIResult(false, "Vui lòng đăng nhập lại", null));
            return;
        }

        shiftApiService.getShifts("Bearer " + token, page, size, status).enqueue(new Callback<List<Shift>>() {
            @Override
            public void onResponse(@NonNull Call<List<Shift>> call, @NonNull Response<List<Shift>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Shift> shifts = response.body();
                    Log.d(TAG, "Shift history retrieved: " + shifts.size() + " shifts");
                    listener.onGetShiftHistoryComplete(new APIResult(
                            true,
                            "Lấy lịch sử ca làm việc thành công",
                            shifts
                    ));
                } else {
                    String errorMessage = handleErrorResponse(response, "Không thể lấy lịch sử ca làm việc");
                    Log.e(TAG, "Get shift history failed: " + errorMessage);
                    listener.onGetShiftHistoryComplete(new APIResult(false, errorMessage, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Shift>> call, @NonNull Throwable t) {
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Get shift history network error: " + errorMessage, t);
                listener.onGetShiftHistoryComplete(new APIResult(false, errorMessage, null));
            }
        });
    }

    /**
     * Helper method to extract error messages from HTTP responses
     */
    private String handleErrorResponse(Response<?> response, String defaultMessage) {
        String errorMessage = defaultMessage + ". Mã lỗi: " + response.code();
        try {
            if (response.errorBody() != null) {
                errorMessage += " - " + response.errorBody().string();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error body: " + e.getMessage());
        }
        return errorMessage;
    }

    /**
     * Get the saved auth token for API requests
     */
    private String getAuthToken() {
        SharedPreferences prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    // Callback Interfaces
    public interface OnStartShiftListener {
        void onStartShiftComplete(APIResult result);
    }

    public interface OnEndShiftListener {
        void onEndShiftComplete(APIResult result);
    }

    public interface OnGetCurrentShiftListener {
        void onGetCurrentShiftComplete(APIResult result);
    }

    public interface OnRecordTransactionListener {
        void onRecordTransactionComplete(APIResult result);
    }

    public interface OnGetCashBalanceListener {
        void onGetCashBalanceComplete(APIResult result);
    }

    public interface OnGetShiftHistoryListener {
        void onGetShiftHistoryComplete(APIResult result);
    }
}
