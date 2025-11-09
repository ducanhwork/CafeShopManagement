package com.group3.application.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.utils.Event;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.repository.AuthRepository;
import com.group3.application.model.webservice.ApiService;

public class ForgotPasswordViewModel extends AndroidViewModel {
    private final MutableLiveData<String> _emailError = new MutableLiveData<>();
    public final LiveData<String> emailError = _emailError;
    private final MutableLiveData<String> _resetPasswordError = new MutableLiveData<>();
    public final LiveData<String> resetPasswordError = _resetPasswordError;
    private final MutableLiveData<Event<APIResult>> _resetPasswordResult = new MutableLiveData<>();
    public final LiveData<Event<APIResult>> resetPasswordResult = _resetPasswordResult;
    private AuthRepository authRepository;


    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public void forgotPassword(String email) {
        _emailError.postValue(null);
        boolean isValid = true;
        // SỬA LỖI: Sử dụng android.text.TextUtils
        if (TextUtils.isEmpty(email)) {
            _emailError.postValue("Email is required.");
            isValid = false;
            // SỬA LỖI: Sử dụng android.util.Patterns
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.postValue("Please enter valid email address.");
            isValid = false;
        }
        if (!isValid) {
            _resetPasswordError.postValue("Internal error.");
        }
        authRepository.forgotPassword(email, listener -> {
            if (listener.isSuccess()) {
                // Xử lý thành công
                _resetPasswordResult.postValue(new Event<>(listener));
            } else {
                _resetPasswordResult.postValue(new Event<>(listener));
                // Xử lý lỗi
            }
        });
    }


}
