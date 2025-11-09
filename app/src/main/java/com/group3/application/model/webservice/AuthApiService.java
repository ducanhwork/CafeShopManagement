package com.group3.application.model.webservice;

import com.group3.application.model.bean.AuthenticationResponse;
import com.group3.application.model.bean.LoginRequest;
import com.group3.application.model.bean.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    
    @POST("auth/login")
    Call<AuthenticationResponse> login(@Body LoginRequest request);
    
    @POST("auth/register")
    Call<AuthenticationResponse> register(@Body RegisterRequest request);
}
