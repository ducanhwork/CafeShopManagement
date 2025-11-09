package com.group3.application.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.model.entity.Reservation;
import com.group3.application.model.repository.ReservationRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationViewModel extends ViewModel {

    private ReservationRepository reservationRepository = new ReservationRepository();

    private final MutableLiveData<List<Reservation>> reservations = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    public LiveData<List<Reservation>> getReservations() {
        return reservations;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchReservationsByTable(String tableId) {
        isLoading.setValue(true);
        reservationRepository.getReservationsByTable(tableId).enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful()) {
                    reservations.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch reservations");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                error.setValue(t.getMessage());
                isLoading.setValue(false);
            }
        });
    }
}
