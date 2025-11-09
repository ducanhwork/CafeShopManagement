package com.group3.application.model.repository;

import com.group3.application.model.entity.Reservation;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;

import retrofit2.Call;

public class ReservationRepository {

    private ApiService apiService;

    public ReservationRepository() {
        apiService = ApiClient.get().create(ApiService.class);
    }

    public Call<List<Reservation>> getReservationsByTable(String tableId) {
        return apiService.getReservationsByTable(tableId);
    }
}
