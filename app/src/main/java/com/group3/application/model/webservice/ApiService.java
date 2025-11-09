package com.group3.application.model.webservice;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.AuthenticationRequest;
import com.group3.application.model.dto.AuthenticationResponse;
import com.group3.application.model.dto.UpdatePassWordRequest;
import com.group3.application.model.entity.Reservation;
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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/tables")
    Call<List<TableInfo>> listTables(
            @Query("status") String status,
            @Query("keyword") String keyword
    );

    @POST("/api/auth/login")
    Call<AuthenticationResponse> login(@Body AuthenticationRequest authenticationRequest);

    @PUT("/api/auth/change-password")
    Call<String> changePassword(@Body UpdatePassWordRequest updatePassWordRequest);

    @POST("/api/auth/reset-password")
    Call<APIResult> resetPassword(@Body String email);

    @GET("api/auth/me")
    Call<User> myProfile(@Header("Authorization") String token);

    @GET("api/reservations/table/{tableId}")
    Call<List<Reservation>> getReservationsByTable(@Path("tableId") String tableId);

    @POST("api/reservations")
    Call<Reservation> createReservation(@Body Reservation reservation);

    @PATCH("api/reservations/cancel/{id}")
    Call<Void> cancelReservation(@Path("id") UUID id);

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

}
