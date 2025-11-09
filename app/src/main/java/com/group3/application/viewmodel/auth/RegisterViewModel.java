package com.group3.application.viewmodel.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.model.dto.AuthResponse;
import com.group3.application.model.repository.AuthRepository;

/**
 * ViewModel for registration screen
 */
public class RegisterViewModel extends ViewModel {
    private final AuthRepository authRepository = new AuthRepository();

    private final MutableLiveData<AuthResponse> registerResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public LiveData<AuthResponse> getRegisterResult() { return registerResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getLoading() { return loading; }

    public void register(String name, String email, String password, String role) {
        loading.setValue(true);
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
    }
}
