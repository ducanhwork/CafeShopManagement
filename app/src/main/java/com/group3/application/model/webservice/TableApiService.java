package com.group3.application.model.webservice;

import com.group3.application.model.bean.BulkCreateResponse;
import com.group3.application.model.bean.CreateTableRequest;
import com.group3.application.model.bean.PagedResponse;
import com.group3.application.model.bean.UpdateTableRequest;
import com.group3.application.model.bean.UpdateTableStatusRequest;
import com.group3.application.model.entity.TableInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API service for table management endpoints
 * Matches backend API specification exactly
 */
public interface TableApiService {
    
    /**
     * Get all tables with optional filtering and pagination
     * @param status Filter by status (Available, Occupied, Reserved)
     * @param location Filter by location (Indoor, Outdoor, Balcony)
     * @param minSeatCount Minimum number of seats
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param sort Sort field,direction (e.g., name,asc)
     * @param token JWT Bearer token
     * @return PagedResponse containing list of tables
     */
    @GET("/api/tables/manager/all")
    Call<PagedResponse<TableInfo>> getTables(
            @Query("status") String status,
            @Query("location") String location,
            @Query("minSeatCount") Integer minSeatCount,
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sort") String sort,
            @Header("Authorization") String token
    );
    
    /**
     * Get single table by ID
     * @param id Table ID (UUID)
     * @param token JWT Bearer token
     */
    @GET("api/tables/{id}")
    Call<TableInfo> getTableById(
            @Path("id") String id,
            @Header("Authorization") String token
    );
    
    /**
     * Create a new table (Waiter, Manager only)
     * @param request Create table request body
     * @param token JWT Bearer token
     */
    @POST("api/tables")
    Call<TableInfo> createTable(
            @Body CreateTableRequest request,
            @Header("Authorization") String token
    );
    
    /**
     * Update existing table (Waiter, Manager only)
     * @param id Table ID to update
     * @param request Update table request body
     * @param token JWT Bearer token
     */
    @PUT("api/tables/{id}")
    Call<TableInfo> updateTable(
            @Path("id") String id,
            @Body UpdateTableRequest request,
            @Header("Authorization") String token
    );
    
    /**
     * Quick update table status only (Waiter, Manager)
     * @param id Table ID to update
     * @param request Status update request body
     * @param token JWT Bearer token
     */
    @PATCH("api/tables/{id}/status")
    Call<TableInfo> updateTableStatus(
            @Path("id") String id,
            @Body UpdateTableStatusRequest request,
            @Header("Authorization") String token
    );
    
    /**
     * Delete table (Manager only)
     * @param id Table ID to delete
     * @param token JWT Bearer token
     */
    @DELETE("api/tables/{id}")
    Call<Void> deleteTable(
            @Path("id") String id,
            @Header("Authorization") String token
    );
    
    /**
     * Bulk create tables (Manager only)
     * @param requests List of create table requests
     * @param token JWT Bearer token
     */
    @POST("api/tables/bulk")
    Call<BulkCreateResponse> bulkCreateTables(
            @Body List<CreateTableRequest> requests,
            @Header("Authorization") String token
    );
    
    /**
     * Get table usage statistics
     * @param token JWT Bearer token
     */
    @GET("api/tables/statistics")
    Call<Object> getTableStatistics(
            @Header("Authorization") String token
    );
    
    /**
     * Get count of available tables
     * @param token JWT Bearer token
     */
    @GET("api/tables/available-count")
    Call<Integer> getAvailableTableCount(
            @Header("Authorization") String token
    );
}
