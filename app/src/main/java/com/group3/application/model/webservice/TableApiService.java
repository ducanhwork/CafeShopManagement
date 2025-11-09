package com.group3.application.model.webservice;

import com.group3.application.model.bean.TableRequest;
import com.group3.application.model.bean.TableResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TableApiService {
    
    @GET("tables")
    Call<List<TableResponse>> getAllTables();
    
    @POST("tables")
    Call<TableResponse> createTable(@Body TableRequest request);
    
    @PUT("tables/{id}")
    Call<TableResponse> updateTable(@Path("id") String id, @Body TableRequest request);
    
    @DELETE("tables/{id}")
    Call<Void> deleteTable(@Path("id") String id);
    
    @GET("tables/status/{status}")
    Call<List<TableResponse>> getTablesByStatus(@Path("status") String status);
}
