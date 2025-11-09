package com.group3.application.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.utils.Event;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.repository.AuthRepository;

public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";

    private MutableLiveData<Event<APIResult>> _viewProfileResult = new MutableLiveData<>();
    public LiveData<Event<APIResult>> viewProfileResult = _viewProfileResult;
    private AuthRepository authRepository;
    private SharedPreferences prefs;
    private MutableLiveData<Event<APIResult>> _logOutResult = new MutableLiveData<>();
    public LiveData<Event<APIResult>> logOutResult = _logOutResult;


    public ProfileViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        prefs = application.getSharedPreferences(LoginViewModel.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void viewProfile() {
        authRepository.getMyProfile(result -> {
            if (result.isSuccess()) {
                _viewProfileResult.postValue(new Event<>(result));
            } else {
                _viewProfileResult.postValue(new Event<>(result));
            }
        });
    }

    public void logOut() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(LoginViewModel.KEY_AUTH_TOKEN);
        editor.remove(LoginViewModel.KEY_IS_LOGGED_IN);
        editor.apply();
        _logOutResult.postValue(new Event<>(new APIResult(true, "Đăng xuất thành công.")));
    }

}
