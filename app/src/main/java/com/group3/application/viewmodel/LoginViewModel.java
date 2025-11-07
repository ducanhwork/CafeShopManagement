package com.group3.application.viewmodel;

import android.app.Application;
import android.content.Context; // SỬA LỖI: Import đúng Context
import android.content.SharedPreferences;
import android.text.TextUtils; // SỬA LỖI: Import đúng TextUtils
import android.util.Patterns; // SỬA LỖI: Thêm import cho Patterns

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel; // SỬA LỖI: Kế thừa từ AndroidViewModel
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.enums.NavigationTarget;
import com.group3.application.model.dto.LoginResult;
import com.group3.application.model.repository.AuthRepository;
import com.group3.application.common.utils.Event; // SỬA LỖI: Import đúng lớp Event wrapper của bạn

// SỬA LỖI: Lớp phải kế thừa từ AndroidViewModel
public class LoginViewModel extends AndroidViewModel {
    private AuthRepository authRepository;
    private SharedPreferences prefs;

    public static final String PREF_NAME = "CafeManagerPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_AUTH_TOKEN = "authToken";

    private MutableLiveData<Event<LoginResult>> _loginResult = new MutableLiveData<>();
    public LiveData<Event<LoginResult>> loginResult = _loginResult;

    private MutableLiveData<Event<NavigationTarget>> _navigationEvent = new MutableLiveData<>();
    public LiveData<Event<NavigationTarget>> navigationEvent = _navigationEvent;

    private MutableLiveData<String> _emailError = new MutableLiveData<>();
    public LiveData<String> emailError = _emailError;

    private MutableLiveData<String> _passwordError = new MutableLiveData<>();
    public LiveData<String> passwordError = _passwordError;


    public LoginViewModel(@NonNull Application application) {
        super(application); // SỬA LỖI: super() bây giờ sẽ hoạt động
        authRepository = new AuthRepository(application);
        // SỬA LỖI: Sử dụng Context.MODE_PRIVATE thay vì import sai
        prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void checkInitialLoginState() {
        if (authRepository.isUserLoggedIn()) { // Dùng repository để kiểm tra
            _navigationEvent.postValue(new Event<>(NavigationTarget.PRODUCT_LIST));
        }
    }

    public void loginUser(String email, String password) {
        _emailError.postValue(null);
        _passwordError.postValue(null);

        boolean isValid = true;

        // SỬA LỖI: Sử dụng android.text.TextUtils
        if (TextUtils.isEmpty(email)) {
            _emailError.postValue("Email là bắt buộc.");
            isValid = false;
            // SỬA LỖI: Sử dụng android.util.Patterns
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.postValue("Vui lòng nhập địa chỉ email hợp lệ.");
            isValid = false;
        }

        // SỬA LỖI: Sử dụng android.text.TextUtils
        if (TextUtils.isEmpty(password)) {
            _passwordError.postValue("Mật khẩu là bắt buộc.");
            isValid = false;
        } else if (password.length() < 6) {
            _passwordError.postValue("Mật khẩu phải có ít nhất 6 ký tự.");
            isValid = false;
        }

        if (!isValid) {
            _loginResult.postValue(new Event<>(new LoginResult(false, "Lỗi xác thực cục bộ.")));
            return;
        }

        authRepository.login(email, password, result -> {
            if (result.isSuccess()) {
                // AuthRepository đã tự lưu token và cờ isLoggedIn
                // Ở đây, bạn có thể lưu userEmail vào SharedPreferences nếu bạn muốn
                saveUserEmailToPrefs(email);
                _loginResult.postValue(new Event<>(result));
                _navigationEvent.postValue(new Event<>(NavigationTarget.PRODUCT_LIST));
            } else {
                _loginResult.postValue(new Event<>(result));
            }
        });
    }

    public void navigateToRegister() {
        _navigationEvent.postValue(new Event<>(NavigationTarget.REGISTER));
    }

    public void navigateToForgotPassword() {
        _navigationEvent.postValue(new Event<>(NavigationTarget.FORGOT_PASSWORD));
    }

    // Hàm tiện ích để lưu email (nếu API không trả về)
    private void saveUserEmailToPrefs(String email) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    // Nếu bạn muốn lấy email đã lưu
    public String getUserEmailFromPrefs() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }
}