package com.group3.application.di;

import dagger.Module;

/**
 * Dagger module for application-level dependencies
 * Note: PreferenceManager uses static methods, no DI needed
 */
@Module
public class AppModule {
    // Application-level dependencies can be added here
    // PreferenceManager uses static methods, so no provider needed
}
