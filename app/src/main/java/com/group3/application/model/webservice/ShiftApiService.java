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
     * @param request StartShiftRequest with openingCash
     * @return Call<Shift> The created shift with OPEN status
     */
    @POST("shifts/start")
    Call<Shift> startShift(@Body StartShiftRequest request);

    /**
     * End the current shift
     * POST /shifts/end
     * 
     * @param request EndShiftRequest with closingCash
     * @return Call<Shift> The closed shift with discrepancy calculated
     */
    @POST("shifts/end")
    Call<Shift> endShift(@Body EndShiftRequest request);

    /**
     * Get the current open shift for the logged-in user
     * GET /shifts/current
     * 
     * @return Call<Shift> The current open shift or null if no shift is open
     */
    @GET("shifts/current")
    Call<Shift> getCurrentShift();

    /**
     * Record a cash transaction (IN/OUT/REFUND) during the current shift
     * POST /shifts/cash/record
     * 
     * @param request RecordTransactionRequest with amount, type, description
     * @return Call<CashTransaction> The recorded transaction with running balance
     */
    @POST("shifts/cash/record")
    Call<CashTransaction> recordCashTransaction(@Body RecordTransactionRequest request);

    /**
     * Get the current cash balance for the active shift
     * GET /shifts/cash/balance
     * 
     * @return Call<CashBalance> Current cash balance with breakdown
     */
    @GET("shifts/cash/balance")
    Call<CashBalance> getCashBalance();

    /**
     * Get shift history with pagination and optional status filter
     * GET /shifts
     * 
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param status Optional status filter (OPEN, CLOSED)
     * @return Call<List<Shift>> Paginated list of shifts
     */
    @GET("shifts")
    Call<List<Shift>> getShifts(
        @Query("page") int page,
        @Query("size") int size,
        @Query("status") String status
    );
}
