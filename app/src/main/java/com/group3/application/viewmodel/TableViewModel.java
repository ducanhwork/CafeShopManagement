package com.group3.application.viewmodel;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.common.base.BaseRepository;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.repository.TableRepository;

import java.util.List;

public class TableViewModel extends ViewModel {

    public enum TableAction { SHOW_CONFIRM_RESERVED, OPEN_ORDER, SHOW_ERROR }

    public static class OneTimeEvent<T> {
        private final T content; 
        private boolean handled = false;
        
        public OneTimeEvent(T c) { content = c; }
        
        public T getIfNotHandled() { 
            if (handled) return null; 
            handled = true; 
            return content; 
        }
    }

    private final TableRepository repo = new TableRepository();

    private final MutableLiveData<List<TableInfo>> tables = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    public LiveData<List<TableInfo>> getTables() { return tables; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }

    /**
     * Load tables with optional filters
     */
    public void loadTables(String status, String keyword) {
        loading.setValue(true);
        error.setValue(null);

        repo.getTables(status, keyword, new BaseRepository.ApiCallback<List<TableInfo>>() {
            @Override
            public void onSuccess(List<TableInfo> data) {
                loading.postValue(false);
                tables.postValue(data);
            }

            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                error.postValue(errorMsg);
            }
        });
    }

    // Events for table actions
    private final MutableLiveData<OneTimeEvent<Pair<TableAction, TableInfo>>> events = new MutableLiveData<>();
    public LiveData<OneTimeEvent<Pair<TableAction, TableInfo>>> getEvents() { return events; }

    /**
     * Handle table click based on status
     */
    public void onTableClicked(TableInfo table) {
        String status = table.getStatus() == null ? "" : table.getStatus().toUpperCase();
        
        switch (status) {
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

    /**
     * Proceed with reserved table
     */
    public void proceedReserved(TableInfo table) {
        events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.OPEN_ORDER, table)));
    }
}
