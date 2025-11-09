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
 * ViewModel for registration screen
 */
public class RegisterViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<AuthResponse> registerResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    
    public RegisterViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<AuthResponse> getRegisterResult() { return registerResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getLoading() { return loading; }

    public void register(String name, String email, String password, String role) {
        loading.setValue(true);
        // TODO: Implement register method in AuthRepository
        errorMessage.postValue("Registration not yet implemented in backend");
        loading.postValue(false);
        /*
        authRepository.register(name, email, password, role, new BaseRepository.ApiCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse data) {
                loading.postValue(false);
                registerResult.postValue(data);
            }

            @Override
            public void onError(String error) {
                loading.postValue(false);
                errorMessage.postValue(error);
            }
        });
        */
    }
}
