package com.group3.application.model.webservice;

import com.group3.application.model.entity.TableInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface  ApiService {
    @GET("api/tables")
    Call<List<TableInfo>> listTables(
            @Query("status") String status,
            @Query("keyword") String keyword
    );
}
