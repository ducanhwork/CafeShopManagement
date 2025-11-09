package com.group3.application.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.group3.application.model.repository.TableRepository;
import com.group3.application.viewmodel.TableViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory for creating ViewModels with dependency injection
 */
@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {
    
    private final TableRepository tableRepository;
    
    @Inject
    public ViewModelFactory(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }
    
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TableViewModel.class)) {
            return (T) new TableViewModel();
        }
        
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
