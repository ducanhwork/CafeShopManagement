package com.group3.application.model.webservice;

import com.group3.application.model.dto.AuthResponse;
import com.group3.application.model.dto.LoginRequest;
import com.group3.application.model.dto.RegisterRequest;
import com.group3.application.model.entity.TableInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API Service interface for Retrofit
 */
public interface ApiService {
    
    // Authentication endpoints
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);
    
    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
    
    @POST("auth/logout")
    Call<Void> logout();
    
    @GET("auth/me")
    Call<AuthResponse.UserInfo> getCurrentUser();
    
    // Table endpoints
    @GET("tables")
    Call<List<TableInfo>> listTables(
            @Query("status") String status,
            @Query("keyword") String keyword
    );
    
    @GET("tables/{id}")
    Call<TableInfo> getTable(@Path("id") Long id);
    
    @POST("tables")
    Call<TableInfo> createTable(@Body TableInfo table);
    
    @PUT("tables/{id}")
    Call<TableInfo> updateTable(@Path("id") Long id, @Body TableInfo table);
    
    @DELETE("tables/{id}")
    Call<Void> deleteTable(@Path("id") Long id);
}
