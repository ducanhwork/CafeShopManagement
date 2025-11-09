package com.group3.application.model.webservice;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit instance;

    public static Retrofit get() {
        if (instance == null) {
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(log)
                    .build();

            instance = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.106:8080/") //
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return instance;
    }
}
