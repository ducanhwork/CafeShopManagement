package com.group3.application.di;

import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Dagger module for network-related dependencies
 */
@Module
public class NetworkModule {
    
    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return ApiClient.get();
    }
    
    @Provides
    @Singleton
    public ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
