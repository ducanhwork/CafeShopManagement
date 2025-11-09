package com.group3.application.model.webservice;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.group3.application.view.adapter.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static volatile Retrofit instance;
    private static volatile ApiService apiService;

    @SuppressLint("StaticFieldLeak")
    private static Context appContext;

    public static void init(Context context) { appContext = context.getApplicationContext(); }

    public static Retrofit get() {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    HttpLoggingInterceptor log = new HttpLoggingInterceptor();
                    log.setLevel(HttpLoggingInterceptor.Level.BODY);

                    Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .create();

                    OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .addInterceptor(log)
                        // .addInterceptor(new AuthInterceptor(appContext)) // nếu có
                        .build();

                    instance = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080/")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                }
            }
        }
        return instance;
    }

    public static Retrofit get(Context ctx) { return get(); }

    public static ApiService getApi() {
        if (apiService == null) {
            synchronized (ApiClient.class) {
                if (apiService == null) {
                    apiService = get().create(ApiService.class);
                }
            }
        }
        return apiService;
    }

    public static ApiService getApi(Context ctx) { return getApi(); }
}
