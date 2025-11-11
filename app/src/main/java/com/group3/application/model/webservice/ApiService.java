package com.group3.application.model.webservice;

import com.group3.application.model.dto.OrderRequest;
import com.group3.application.model.dto.OrderUpdateDTO;
import com.group3.application.model.dto.PeriodItemReportDTO;
import com.group3.application.model.dto.ProductForOrder;
import com.group3.application.model.dto.RevenueReportDTO;
import com.group3.application.model.entity.Category;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.AuthenticationRequest;
import com.group3.application.model.dto.AuthenticationResponse;
import com.group3.application.model.dto.UpdatePassWordRequest;
import com.group3.application.model.entity.Order;
import com.group3.application.model.entity.Product;
import com.group3.application.model.entity.Category;
import com.group3.application.model.entity.Product;
import com.group3.application.model.entity.Reservation;
import com.group3.application.model.entity.Role;
import com.group3.application.model.bean.LoyaltyMemberDetailResponse;
import com.group3.application.model.bean.LoyaltyMemberListItem;
import com.group3.application.model.bean.PointsHistoryItem;
import com.group3.application.model.bean.UpdateLoyaltyMemberRequest;
import com.group3.application.model.bean.VoucherRequest;
import com.group3.application.model.bean.VoucherResponse;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/tables")
    Call<List<TableInfo>> listTables(
            @Query("status") String status,
            @Query("keyword") String keyword
    );

    @PUT("api/tables/{id}/status")
    Call<APIResult> updateTableStatus(
        @Header("Authorization") String authToken,
        @Path("id") String tableId,
        @Query("status") String status
    );

    @GET("api/products")
    Call<List<ProductForOrder>> listProductsForOrder(
            @Query("status") String status,
            @Query("categoryId") String categoryId,
            @Query("keyword") String keyword
    );

    @GET("api/categories")
    Call<List<Category>> getCategories();

    @POST("api/orders")
    Call<APIResult<Object>> createOrder(@Header("Authorization") String authToken, @Body OrderRequest orderRequest);

    @GET("api/orders")
    Call<List<Order>> getOrders(
            @Header("Authorization") String authToken,
            @Query("status") String status,
            @Query("tableId") String tableId,
            @Query("staffId") String staffId
    );

    // SỬA: Hoàn trả lại kiểu dữ liệu gốc mà server trả về
    @GET("api/orders/{id}")
    Call<Order> getOrderDetails(
            @Header("Authorization") String authToken,
            @Path("id") String orderId
    );

    @PUT("api/orders/{id}")
    Call<APIResult> updateOrder(
            @Header("Authorization") String authToken,
            @Path("id") String orderId,
            @Body OrderUpdateDTO updateData
    );

    @PUT("api/orders/{id}/items")
    Call<APIResult<Object>> updateOrderItems(
            @Header("Authorization") String authToken,
            @Path("id") String orderId,
            @Body OrderRequest orderRequest
    );

    @GET("api/users")
    Call<List<User>> getAllUsers(@Header("Authorization") String authToken);

    @POST("api/auth/login")
    Call<AuthenticationResponse> login(@Body AuthenticationRequest authenticationRequest);

    @PUT("/api/auth/change-password")
    Call<APIResult> changePassword(@Body UpdatePassWordRequest updatePassWordRequest);

    @POST("/api/auth/reset-password")
    Call<APIResult> resetPassword(@Body String email);

    @GET("api/auth/me")
    Call<User> myProfile(@Header("Authorization") String token);

    @GET("/api/product")
    Call<List<Product>> listProducts(@Header("Authorization") String token, @Query("keyword") String keyword, @Query("category") String category);

    @PUT("/api/product/update-status/{productId}")
    Call<APIResult> updateProductStatus(@Header("Authorization") String token, @Path("productId") UUID productId, @Query("status") Boolean status);

    @Multipart
    @PUT("api/product/update/{productId}")
    Call<Product> updateProduct(
            @Path("productId") UUID productId,
            @Part("product") RequestBody productJson,
            @Part MultipartBody.Part image,
            @Header("Authorization") String token
    );

    @Multipart
    @POST("api/product/add")
    Call<Product> createProduct(
            @Part("product") RequestBody productJson,
            @Part MultipartBody.Part image,
            @Header("Authorization") String token
    );

    @GET("/api/category")
    Call<List<Category>> listCategories(@Header("Authorization") String token);

    @GET("api/reservations/table/{tableId}")
    Call<List<Reservation>> getReservationsByTable(@Path("tableId") String tableId);

    @POST("api/reservations")
    Call<Reservation> createReservation(@Body Reservation reservation);

    @PATCH("api/reservations/cancel/{id}")
    Call<Void> cancelReservation(@Path("id") UUID id);

    @GET("api/users")
    Call<List<User>> getAllUsers();

    @POST("api/users")
    Call<User> createUser(@Body User newStaff);

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

    @GET("api/reports/revenue")
    Call<RevenueReportDTO> getRevenueReport(
            @Header("Authorization") String authToken,
            @Query("dateFrom") String dateFrom,
            @Query("dateTo") String dateTo,
            @Query("filterBy") String filterBy
    );

    @GET("api/reports/items")
    Call<List<PeriodItemReportDTO>> getItemReport(
            @Header("Authorization") String authToken,
            @Query("dateFrom") String dateFrom,
            @Query("dateTo") String dateTo,
            @Query("filterBy") String filterBy
    );
}
