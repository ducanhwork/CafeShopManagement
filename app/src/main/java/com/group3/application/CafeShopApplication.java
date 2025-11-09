package com.group3.application;

import android.app.Application;

import com.group3.application.di.AppComponent;
import com.group3.application.di.DaggerAppComponent;
import com.group3.application.model.webservice.ApiClient;

/**
 * Custom Application class
 * Initializes Dagger and API Client
 */
public class CafeShopApplication extends Application {
    
    private AppComponent appComponent;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Dagger
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build();
        appComponent.inject(this);
        
        // Initialize API Client with application context
        ApiClient.init(this);
    }
    
    /**
     * Get Dagger component for dependency injection
     */
    public AppComponent getAppComponent() {
        return appComponent;
    }
}
