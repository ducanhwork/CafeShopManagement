package com.group3.application.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.repository.TableRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationViewModel extends ViewModel {
    private TableRepository tableRepository = new TableRepository();
    private MutableLiveData<List<TableInfo>> tables = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<TableInfo>> getTables() {
        return tables;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchTables() {
        isLoading.setValue(true);
        tableRepository.getTables(null, null).enqueue(new Callback<List<TableInfo>>() {
            @Override
            public void onResponse(Call<List<TableInfo>> call, Response<List<TableInfo>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    tables.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch tables " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<TableInfo>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }
}
