package com.group3.application.viewmodel;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.repository.TableRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TableViewModel extends ViewModel {

    public enum TableAction { SHOW_CONFIRM_RESERVED, OPEN_ORDER, SHOW_ERROR }

    public class OneTimeEvent<T> {
        private final T content; private boolean handled=false;
        public OneTimeEvent(T c){content=c;}
        public T getIfNotHandled(){ if(handled) return null; handled=true; return content; }
    }

    private final TableRepository repo = new TableRepository();

    private final MutableLiveData<List<TableInfo>> tables = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    private Call<List<TableInfo>> inFlight;

    public LiveData<List<TableInfo>> getTables() { return tables; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }

    public void loadTables(String status, String keyword) {
        if (inFlight != null) inFlight.cancel();
        loading.setValue(true);
        error.setValue(null);

        inFlight = repo.getTables(status, keyword);
        inFlight.enqueue(new Callback<List<TableInfo>>() {
            @Override public void onResponse(Call<List<TableInfo>> call, Response<List<TableInfo>> resp) {
                loading.postValue(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    tables.postValue(resp.body());
                } else {
                    error.postValue("Load failed: " + resp.code());
                }
            }
            @Override public void onFailure(Call<List<TableInfo>> call, Throwable t) {
                if (call.isCanceled()) return;
                loading.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }

    @Override protected void onCleared() {
        if (inFlight != null) inFlight.cancel();
    }

    private final MutableLiveData<OneTimeEvent<Pair<TableAction, TableInfo>>> events = new MutableLiveData<>();
    public LiveData<OneTimeEvent<Pair<TableAction, TableInfo>>> getEvents() { return events; }

    public void onTableClicked(TableInfo table){
        String st = table.getStatus()==null? "" : table.getStatus().toUpperCase();
        switch (st){
            case "RESERVED":
                events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.SHOW_CONFIRM_RESERVED, table)));
                break;
            case "EMPTY":
            case "AVAILABLE":
            case "SERVING":
            case "WAITING_BILL":
                events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.OPEN_ORDER, table)));
                break;
            default:
                events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.SHOW_ERROR, table)));
        }
    }

    public void proceedReserved(TableInfo table){
        events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.OPEN_ORDER, table)));
    }
}
