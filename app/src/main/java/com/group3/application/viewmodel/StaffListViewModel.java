package com.group3.application.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.model.entity.User;
import com.group3.application.model.repository.UserRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffListViewModel extends ViewModel {

    private UserRepository staffRepository = new UserRepository();

    private final MutableLiveData<List<User>> staffs = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
//    private final MutableLiveData<User> createdReservation = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> _reservationCancelled = new MutableLiveData<>(false);

    public LiveData<List<User>> getStaffs() {
        return staffs;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

//    public LiveData<User> getCreatedReservation() {
//        return createdReservation;
//    }

//    public LiveData<Boolean> getReservationCancelled() {
//        return _reservationCancelled;
//    }

    public void fetchStaff() {
        isLoading.setValue(true);
        staffRepository.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    staffs.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch staffs");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                error.setValue(throwable.getMessage());
                isLoading.setValue(false);
            }
        });
    }
}
