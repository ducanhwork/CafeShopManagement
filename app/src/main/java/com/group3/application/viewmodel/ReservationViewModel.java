package com.group3.application.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.model.entity.Reservation;
import com.group3.application.model.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationViewModel extends ViewModel {

    private ReservationRepository reservationRepository = new ReservationRepository();

    private final MutableLiveData<List<Reservation>> reservations = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<Reservation> createdReservation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reservationCancelled = new MutableLiveData<>(false);

    public LiveData<List<Reservation>> getReservations() {
        return reservations;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Reservation> getCreatedReservation() {
        return createdReservation;
    }

    public LiveData<Boolean> getReservationCancelled() {
        return reservationCancelled;
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

    public void createReservation(String customerName, String customerPhone, String reservationTimeString, int numGuests, String tableId, String userId) {
        isLoading.setValue(true);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime reservationTime = LocalDateTime.parse(reservationTimeString, formatter);
        Reservation reservation = new Reservation(null, customerName, customerPhone, reservationTime, numGuests, "confirmed", null, UUID.fromString(tableId), UUID.fromString(userId));

        reservationRepository.createReservation(reservation).enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    createdReservation.setValue(response.body());
                } else {
                    error.setValue("Failed to create reservation");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                error.setValue(t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void cancelReservation(Reservation reservation) {
        isLoading.setValue(true);
        reservationRepository.cancelReservation(reservation.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    reservationCancelled.setValue(true);
                } else {
                    error.setValue("Failed to cancel reservation");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage());
                isLoading.setValue(false);
            }
        });
    }
}
