package com.group3.application.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.enums.NavigationTarget;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.repository.AuthRepository;
import com.group3.application.common.utils.Event;

public class LoginViewModel extends AndroidViewModel {
    private AuthRepository authRepository;
    private SharedPreferences prefs;

    // --- Public Constants for SharedPreferences ---
    public static final String PREF_NAME = "CafeManagerPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_AUTH_TOKEN = "authToken";
    public static final String KEY_USER = "userJson"; // Thêm KEY_USER

    private MutableLiveData<Event<APIResult>> _loginResult = new MutableLiveData<>();
    public LiveData<Event<APIResult>> loginResult = _loginResult;

    private MutableLiveData<Event<NavigationTarget>> _navigationEvent = new MutableLiveData<>();
    public LiveData<Event<NavigationTarget>> navigationEvent = _navigationEvent;

    private MutableLiveData<String> _emailError = new MutableLiveData<>();
    public LiveData<String> emailError = _emailError;

    private MutableLiveData<String> _passwordError = new MutableLiveData<>();
    public LiveData<String> passwordError = _passwordError;


    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void checkInitialLoginState() {
        if (authRepository.isUserLoggedIn()) {
            _navigationEvent.postValue(new Event<>(NavigationTarget.PRODUCT_LIST));
        }
    }

    public void loginUser(String email, String password) {
        _emailError.postValue(null);
        _passwordError.postValue(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            _emailError.postValue("Email là bắt buộc.");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.postValue("Vui lòng nhập địa chỉ email hợp lệ.");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            _passwordError.postValue("Mật khẩu là bắt buộc.");
            isValid = false;
        } else if (password.length() < 6) {
            _passwordError.postValue("Mật khẩu phải có ít nhất 6 ký tự.");
            isValid = false;
        }

        if (!isValid) {
            _loginResult.postValue(new Event<>(new APIResult(false, "Lỗi xác thực cục bộ.")));
            return;
        }

        authRepository.login(email, password, result -> {
            if (result.isSuccess()) {
                saveUserEmailToPrefs(email);
                _loginResult.postValue(new Event<>(result));
                _navigationEvent.postValue(new Event<>(NavigationTarget.ADMIN_PAGE));
            } else {
                _loginResult.postValue(new Event<>(result));
            }
        });
    }

    public void navigateToForgotPassword() {
        _navigationEvent.postValue(new Event<>(NavigationTarget.FORGOT_PASSWORD));
    }

    private void saveUserEmailToPrefs(String email) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public String getUserEmailFromPrefs() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }
}
