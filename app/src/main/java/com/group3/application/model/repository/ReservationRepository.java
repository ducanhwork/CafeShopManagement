package com.group3.application.model.repository;

import com.group3.application.model.entity.Reservation;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class ReservationRepository {

    private ApiService apiService;

    public ReservationRepository() {
        apiService = ApiClient.get().create(ApiService.class);
    }

    public Call<List<Reservation>> getReservationsByTable(String tableId) {
        return apiService.getReservationsByTable(tableId);
    }

    public Call<Reservation> createReservation(Reservation reservation) {
        return apiService.createReservation(reservation);
    }
    
    public Call<Void> cancelReservation(UUID id) {
        return apiService.cancelReservation(id);
    }
}
