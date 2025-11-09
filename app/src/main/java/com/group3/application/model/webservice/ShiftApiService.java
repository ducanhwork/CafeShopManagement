package com.group3.application.model.webservice;

import com.group3.application.model.dto.EndShiftRequest;
import com.group3.application.model.dto.RecordTransactionRequest;
import com.group3.application.model.dto.StartShiftRequest;
import com.group3.application.model.entity.CashBalance;
import com.group3.application.model.entity.CashTransaction;
import com.group3.application.model.entity.Shift;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit API service interface for Shift Management endpoints.
 * Base URL: http://10.0.2.2:8080/api
 * All endpoints require JWT Bearer token authentication.
 */
public interface ShiftApiService {

    /**
     * Start a new shift
     * POST /shifts/start
     * 
     * @param token JWT Bearer token for authentication
     * @param request StartShiftRequest with openingCash
     * @return Call<Shift> The created shift with OPEN status
     */
    @POST("api/shifts/start")
    Call<Shift> startShift(@Header("Authorization") String token, @Body StartShiftRequest request);

    /**
     * End the current shift
     * POST /shifts/end
     * 
     * @param token JWT Bearer token for authentication
     * @param request EndShiftRequest with closingCash
     * @return Call<Shift> The closed shift with discrepancy calculated
     */
    @POST("api/shifts/end")
    Call<Shift> endShift(@Header("Authorization") String token, @Body EndShiftRequest request);

    /**
     * Get the current open shift for the logged-in user
     * GET /shifts/current
     * 
     * @param token JWT Bearer token for authentication
     * @return Call<Shift> The current open shift or null if no shift is open
     */
    @GET("api/shifts/current")
    Call<Shift> getCurrentShift(@Header("Authorization") String token);

    /**
     * Record a cash transaction (IN/OUT/REFUND) during the current shift
     * POST /shifts/cash/record
     * 
     * @param token JWT Bearer token for authentication
     * @param request RecordTransactionRequest with amount, type, description
     * @return Call<CashTransaction> The recorded transaction with running balance
     */
    @POST("api/shifts/cash/record")
    Call<CashTransaction> recordCashTransaction(@Header("Authorization") String token, @Body RecordTransactionRequest request);

    /**
     * Get the current cash balance for the active shift
     * GET /shifts/cash/balance
     * 
     * @param token JWT Bearer token for authentication
     * @return Call<CashBalance> Current cash balance with breakdown
     */
    @GET("api/shifts/cash/balance")
    Call<CashBalance> getCashBalance(@Header("Authorization") String token);

    /**
     * Get shift history with pagination and optional status filter
     * GET /shifts
     * 
     * @param token JWT Bearer token for authentication
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param status Optional status filter (OPEN, CLOSED)
     * @return Call<List<Shift>> Paginated list of shifts
     */
    @GET("api/shifts")
    Call<List<Shift>> getShifts(
        @Header("Authorization") String token,
        @Query("page") int page,
        @Query("size") int size,
        @Query("status") String status
    );
}
