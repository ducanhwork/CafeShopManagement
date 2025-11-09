package com.group3.application.di;

import android.content.Context;

import com.group3.application.CafeShopApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 * Main Dagger component for the application
 * Provides application-wide dependencies
 */
@Singleton
@Component(modules = {
    AppModule.class,
    NetworkModule.class,
    RepositoryModule.class
})
public interface AppComponent {
    
    void inject(CafeShopApplication application);
    
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Context context);
        
        AppComponent build();
    }
}
