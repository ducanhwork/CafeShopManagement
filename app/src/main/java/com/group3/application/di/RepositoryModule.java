package com.group3.application.di;

import com.group3.application.model.repository.TableRepository;
import com.group3.application.model.webservice.ApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for repository dependencies
 */
@Module
public class RepositoryModule {
    
    @Provides
    @Singleton
    public TableRepository provideTableRepository(ApiService apiService) {
        return new TableRepository();
    }
}
