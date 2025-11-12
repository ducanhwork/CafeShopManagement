package com.group3.application.viewmodel;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.repository.TableRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TableViewModel extends AndroidViewModel {

    public enum TableAction { SHOW_CONFIRM_RESERVED, OPEN_ORDER, SHOW_ERROR_SINGLE, SHOW_ERROR_MULTI }

    public class OneTimeEvent<T> {
        private final T content; private boolean handled=false;
        public OneTimeEvent(T c){content=c;}
        public T getIfNotHandled(){ if(handled) return null; handled=true; return content; }
    }

    private final TableRepository repo;

    public TableViewModel(@NonNull Application application) {
        super(application);
        this.repo = new TableRepository(application.getApplicationContext());
    }

    private final MutableLiveData<List<TableInfo>> tables = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    private Call<List<TableInfo>> inFlight;

    public LiveData<List<TableInfo>> getTables() { return tables; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    private final MutableLiveData<List<TableInfo>> selectedTables = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<TableInfo>> getSelectedTables() { return selectedTables; }

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

    private final MutableLiveData<OneTimeEvent<Pair<TableAction, List<TableInfo>>>> events = new MutableLiveData<>();
    public LiveData<OneTimeEvent<Pair<TableAction, List<TableInfo>>>> getEvents() { return events; }

    // SỬA: Thêm phương thức để chọn các bàn ban đầu bằng ID
    public void selectTablesByIds(List<String> tableIds) {
        if (tableIds == null || tableIds.isEmpty()) {
            return;
        }
        List<TableInfo> allTables = tables.getValue();
        if (allTables == null || allTables.isEmpty()) {
            return; // Chưa có danh sách bàn để tìm kiếm
        }

        List<TableInfo> initialSelection = allTables.stream()
                .filter(table -> tableIds.contains(table.getId()))
                .collect(Collectors.toList());

        selectedTables.setValue(initialSelection);
    }

    public void onTableClicked(TableInfo table) {
        List<TableInfo> currentSelection = selectedTables.getValue();
        if (currentSelection == null) currentSelection = new ArrayList<>();

        String st = table.getStatus() == null ? "" : table.getStatus().toUpperCase();

        // Kiểm tra xem bàn có được phép chọn không
        switch (st) {
            case "EMPTY":
            case "AVAILABLE":
            case "RESERVED": // Cho phép chọn bàn RESERVED (sẽ hỏi sau)

                // Toggle (Thêm/bớt)
                if (currentSelection.contains(table)) {
                    currentSelection.remove(table);
                } else {
                    currentSelection.add(table);
                }
                selectedTables.setValue(currentSelection); // Kích hoạt LiveData
                break;

            case "SERVING":
            case "WAITING_BILL":
                // Bàn đang phục vụ không cho gộp
                // Gửi sự kiện lỗi cho 1 bàn
                events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.SHOW_ERROR_SINGLE, List.of(table))));
                break;
            default:
                // Các trạng thái khác (VD: "DIRTY", "MAINTENANCE")
                events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.SHOW_ERROR_SINGLE, List.of(table))));
        }
    }

    public void confirmSelection() {
        List<TableInfo> currentSelection = selectedTables.getValue();
        if (currentSelection == null || currentSelection.isEmpty()) {
            // Gửi sự kiện lỗi chung
            events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.SHOW_ERROR_MULTI, null)));
            return;
        }

        // Kiểm tra xem có bàn nào đang "RESERVED" không
        boolean hasReserved = false;
        for (TableInfo table : currentSelection) {
            if ("RESERVED".equalsIgnoreCase(table.getStatus())) {
                hasReserved = true;
                break;
            }
        }

        if (hasReserved) {
            // Gửi sự kiện yêu cầu xác nhận cho toàn bộ danh sách
            events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.SHOW_CONFIRM_RESERVED, currentSelection)));
        } else {
            // Không có bàn nào RESERVED, mở order trực tiếp
            events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.OPEN_ORDER, currentSelection)));
        }
    }

    public void proceedReserved(List<TableInfo> tablesToOpen) {
        events.setValue(new OneTimeEvent<>(new Pair<>(TableAction.OPEN_ORDER, tablesToOpen)));
    }
}
