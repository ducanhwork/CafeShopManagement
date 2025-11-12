package com.group3.application.model.webservice;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.AuthenticationRequest;
import com.group3.application.model.dto.AuthenticationResponse;
import com.group3.application.model.dto.CashBalanceResponse;
import com.group3.application.model.dto.CashTransactionRequest;
import com.group3.application.model.dto.CategoryDTO;
import com.group3.application.model.dto.CreateTableRequest;
import com.group3.application.model.dto.EndShiftRequest;
import com.group3.application.model.dto.IncomingStockRequest;
import com.group3.application.model.dto.IngredientRequest;
import com.group3.application.model.dto.PageResponse;
import com.group3.application.model.dto.StartShiftRequest;
import com.group3.application.model.dto.UpdatePassWordRequest;
import com.group3.application.model.dto.UpdateTableRequest;
import com.group3.application.model.dto.UserCreateRequest;
import com.group3.application.model.entity.CashTransaction;
import com.group3.application.model.entity.Category;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.model.entity.LowStockNotification;
import com.group3.application.model.entity.Product;
import com.group3.application.model.entity.Reservation;
import com.group3.application.model.entity.Role;
import com.group3.application.model.bean.LoyaltyMemberDetailResponse;
import com.group3.application.model.bean.LoyaltyMemberListItem;
import com.group3.application.model.bean.PointsHistoryItem;
import com.group3.application.model.bean.UpdateLoyaltyMemberRequest;
import com.group3.application.model.bean.VoucherRequest;
import com.group3.application.model.bean.VoucherResponse;
import com.group3.application.model.entity.Shift;
import com.group3.application.model.entity.StockTransaction;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/tables")
    Call<List<TableInfo>> listTables(@Query("status") String status, @Query("keyword") String keyword);

    @POST("/api/auth/login")
    Call<AuthenticationResponse> login(@Body AuthenticationRequest authenticationRequest);

    @PUT("/api/auth/change-password")
    Call<APIResult> changePassword(@Body UpdatePassWordRequest updatePassWordRequest);

    @POST("/api/auth/reset-password")
    Call<APIResult> resetPassword(@Body String email);

    @GET("api/auth/me")
    Call<User> myProfile(@Header("Authorization") String token);

    @GET("/api/products/manage")
    Call<List<Product>> listProducts(@Header("Authorization") String token, @Query("keyword") String keyword, @Query("category") String category);

    @PUT("/api/products/manage/update-status/{productId}")
    Call<APIResult> updateProductStatus(@Header("Authorization") String token, @Path("productId") UUID productId, @Query("status") Boolean status);

    @Multipart
    @PUT("api/products/manage/update/{productId}")
    Call<Product> updateProduct(
            @Path("productId") UUID productId,
            @Part("product") RequestBody productJson,
            @Part MultipartBody.Part image,
            @Header("Authorization") String token
    );

    @Multipart
    @POST("api/products/manage/add")
    Call<Product> createProduct(
            @Part("product") RequestBody productJson,
            @Part MultipartBody.Part image,
            @Header("Authorization") String token
    );

    @GET("/api/categories")
    Call<List<CategoryDTO>> listCategories(@Header("Authorization") String token);

    @GET("api/reservations/table/{tableId}")
    Call<List<Reservation>> getReservationsByTable(@Path("tableId") String tableId);

    @POST("api/reservations")
    Call<Reservation> createReservation(@Body Reservation reservation, @Header("Authorization") String token);

    @PATCH("api/reservations/cancel/{id}")
    Call<Void> cancelReservation(@Path("id") UUID id);

    @GET("api/users")
    Call<List<User>> getAllUsers();

    @POST("api/users/manage")
    Call<UserCreateRequest> createUser(@Body UserCreateRequest newStaff);

    @PUT("api/users/{id}")
    Call<User> updateUser(@Path("id") String id, @Body User user);

    @GET("api/roles")
    Call<List<Role>> getRoles();

    @POST("api/v1/vouchers")
    Call<VoucherResponse> createVoucher(@Body VoucherRequest request);

    @GET("/api/v1/vouchers")
    Call<List<VoucherResponse>> listVouchers(
            @Query("code") String codeLike,
            @Query("status") String status,
            @Query("type") String type,
            @Query("sortBy") String sortBy
    );

    @GET("/api/v1/vouchers/{id}")
    Call<VoucherResponse> getVoucher(@Path("id") String id);

    @PATCH("/api/v1/vouchers/{id}")
    Call<VoucherResponse> patchVoucher(@Path("id") String id,
                                       @Body Map<String, Object> fields);

    @GET("api/v1/loyalty-members")
    Call<List<LoyaltyMemberListItem>> listLoyaltyMembers(
            @Query("q") String query,
            @Query("sortBy") String sortBy
    );

    @PATCH("api/v1/loyalty-members/{customerId}")
    Call<LoyaltyMemberDetailResponse> editLoyaltyMember(
            @Path("customerId") UUID customerId,
            @Body UpdateLoyaltyMemberRequest request
    );

    @GET("api/v1/loyalty-members/{customerId}/points-history")
    Call<List<PointsHistoryItem>> getPointsHistory(
            @Path("customerId") UUID customerId
    );

    // ==================== SHIFT MANAGEMENT ENDPOINTS ====================
    
    @POST("api/shifts/start")
    Call<Shift> startShift(@Header("Authorization") String token, @Body StartShiftRequest request);

    @GET("api/shifts/current")
    Call<Shift> getCurrentShift(@Header("Authorization") String token);

    @POST("api/shifts/end")
    Call<Shift> endShift(@Header("Authorization") String token, @Body EndShiftRequest request);

    @POST("api/shifts/cash/record")
    Call<CashTransaction> recordCashTransaction(@Header("Authorization") String token, @Body CashTransactionRequest request);

    @GET("api/shifts/cash/balance")
    Call<CashBalanceResponse> getCashBalance(@Header("Authorization") String token);

    @GET("api/shifts/{shiftId}/cash-transactions")
    Call<List<CashTransaction>> getCashTransactions(@Header("Authorization") String token, @Path("shiftId") UUID shiftId);

    @GET("api/shifts")
    Call<PageResponse<Shift>> getAllShifts(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("userId") UUID userId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    // ==================== TABLE MANAGEMENT ENDPOINTS ====================
    
    @GET("api/tables/manager/all")
    Call<PageResponse<TableInfo>> getAllTablesWithPagination(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("location") String location,
            @Query("minSeatCount") Integer minSeatCount,
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sort") String sort
    );

    @GET("api/tables/{id}")
    Call<TableInfo> getTableById(@Header("Authorization") String token, @Path("id") String id);

    @POST("api/tables")
    Call<TableInfo> createTable(@Header("Authorization") String token, @Body CreateTableRequest request);

    @PUT("api/tables/{id}")
    Call<TableInfo> updateTable(@Header("Authorization") String token, @Path("id") String id, @Body UpdateTableRequest request);

    @PATCH("api/tables/{id}/status")
    Call<TableInfo> updateTableStatus(@Header("Authorization") String token, @Path("id") String id, @Body Map<String, String> status);

    @DELETE("api/tables/{id}")
    Call<Map<String, String>> deleteTable(@Header("Authorization") String token, @Path("id") String id);

    // ==================== INVENTORY MANAGEMENT ENDPOINTS ====================
    
    @POST("api/inventory/ingredients")
    Call<Ingredient> addIngredient(@Header("Authorization") String token, @Body IngredientRequest request);

    @PUT("api/inventory/ingredients/{id}")
    Call<Ingredient> updateIngredient(@Header("Authorization") String token, @Path("id") UUID id, @Body IngredientRequest request);

    @DELETE("api/inventory/ingredients/{id}")
    Call<Void> deleteIngredient(@Header("Authorization") String token, @Path("id") UUID id);

    @GET("api/inventory/ingredients/{id}")
    Call<Ingredient> getIngredient(@Header("Authorization") String token, @Path("id") UUID id);

    @GET("api/inventory/ingredients")
    Call<List<Ingredient>> listIngredients(@Header("Authorization") String token);

    @GET("api/inventory/ingredients/search")
    Call<List<Ingredient>> searchIngredients(@Header("Authorization") String token, @Query("name") String name);

    @POST("api/inventory/stock/incoming")
    Call<StockTransaction> addIncomingStock(@Header("Authorization") String token, @Body IncomingStockRequest request);

    @GET("api/inventory/transactions/{productId}")
    Call<List<StockTransaction>> getTransactionHistory(@Header("Authorization") String token, @Path("productId") UUID productId);

    @GET("api/inventory/low-stock")
    Call<List<LowStockNotification>> getLowStockNotifications(@Header("Authorization") String token);

}
