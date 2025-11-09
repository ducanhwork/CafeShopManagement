package com.group3.application.viewmodel.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.model.dto.AuthResponse;
import com.group3.application.model.repository.AuthRepository;

/**
 * ViewModel for login screen
 */
public class LoginViewModel extends ViewModel {
    
    private final AuthRepository authRepository = new AuthRepository();
    
    private final MutableLiveData<AuthResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    
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
        
        authRepository.login(email, password, new BaseRepository.ApiCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse data) {
                loading.postValue(false);
                loginResult.postValue(data);
            }
            
            @Override
            public void onError(String error) {
                loading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
}
