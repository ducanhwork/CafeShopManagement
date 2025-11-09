package com.group3.application.model.webservice;

import android.content.SharedPreferences;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.AuthenticationRequest;
import com.group3.application.model.dto.AuthenticationResponse;
import com.group3.application.model.dto.UpdatePassWordRequest;
import com.group3.application.model.entity.Reservation;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
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
}
