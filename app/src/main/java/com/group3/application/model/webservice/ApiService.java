package com.group3.application.model.webservice;

import com.group3.application.model.dto.OrderRequest;
import com.group3.application.model.entity.Category;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.AuthenticationRequest;
import com.group3.application.model.dto.AuthenticationResponse;
import com.group3.application.model.dto.UpdatePassWordRequest;
import com.group3.application.model.entity.Order;
import com.group3.application.model.entity.Product;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/tables?for=order")
    Call<List<TableInfo>> listTables(
            @Query("status") String status,
            @Query("keyword") String keyword
    );

    @GET("api/products")
    Call<java.util.List<Product>> listProducts(
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

    @PUT("api/auth/change-password")
    Call<String> changePassword(@Body UpdatePassWordRequest updatePassWordRequest);

    @POST("api/auth/reset-password")
    Call<APIResult> resetPassword(@Body String email);

    @GET("api/auth/me")
    Call<User> myProfile(@Header("Authorization") String token);
}
