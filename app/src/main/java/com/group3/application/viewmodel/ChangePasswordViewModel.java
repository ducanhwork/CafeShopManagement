package com.group3.application.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.utils.Event;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.repository.AuthRepository;

public class ChangePasswordViewModel extends AndroidViewModel {
    private static final String TAG = "ChangePasswordViewModel";
    private AuthRepository authRepository;

    private MutableLiveData<String> _crrPasswordError = new MutableLiveData<>();
    public LiveData<String> crrPasswordError = _crrPasswordError;

    private MutableLiveData<String> _newPasswordError = new MutableLiveData<>();
    public LiveData<String> newPasswordError = _newPasswordError;

    private MutableLiveData<String> _confirmNewPasswordError = new MutableLiveData<>();
    public LiveData<String> confirmNewPasswordError = _confirmNewPasswordError;

    private MutableLiveData<Event<APIResult>> _resultChangePassWord = new MutableLiveData<>();
    public LiveData<Event<APIResult>> resultChangePassWord = _resultChangePassWord;

    public ChangePasswordViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public void changePassword(String crrPassword, String newPassword, String confirmNewPassword) {
        if (crrPassword.isEmpty()) {
            _crrPasswordError.postValue("Current password is required");
            return;
        }
        if (newPassword.isEmpty()) {
            _newPasswordError.postValue("New password is required");
            return;
        }
        if (newPassword.length() < 8) {
            _newPasswordError.postValue("New password must be at least 8 characters");
            return;
        }
        if (confirmNewPassword.isEmpty()) {
            _confirmNewPasswordError.postValue("Confirm new password is required");
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            _confirmNewPasswordError.postValue("New password and confirm new password do not match");
            return;
        }
        authRepository.changePassword(crrPassword, newPassword, confirmNewPassword, result -> {
            if (result.isSuccess()) {
                _resultChangePassWord.postValue(new Event<>(result));
            } else {
                Log.e(TAG, "changePassword: " + result.getMessage());
                _resultChangePassWord.postValue(new Event<>(result));
            }
        });


    }
}
