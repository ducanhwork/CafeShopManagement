package com.group3.application.viewmodel.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.model.dto.AuthResponse;
import com.group3.application.model.repository.AuthRepository;

/**
 * ViewModel for login screen
 */
public class LoginViewModel extends AndroidViewModel {
    
    private final AuthRepository authRepository;
    
    private final MutableLiveData<AuthResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    
    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }
    
    public LiveData<AuthResponse> getLoginResult() {
        return loginResult;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    /**
     * Perform login
     */
    public void login(String email, String password) {
        loading.setValue(true);
        
        authRepository.login(email, password, result -> {
            loading.postValue(false);
            if (result.isSuccess()) {
                // TODO: Convert APIResult to AuthResponse if needed
                // For now, just post success message
                errorMessage.postValue(null);
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }
}
