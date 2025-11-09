package com.group3.application.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.common.validator.ValidationResult;
import com.group3.application.model.bean.VoucherRequest;
import com.group3.application.model.bean.VoucherResponse;
import com.group3.application.model.repository.VoucherRepository;
import com.group3.application.common.validator.VoucherValidator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherViewModel extends ViewModel {

    public static class ViewState<T> {
        public enum Status { IDLE, LOADING, SAVING, LOADED, SUCCESS, ERROR }

        @NonNull
        public final Status status;
        @Nullable
        public final T data;
        @Nullable
        public final String message;

        public ViewState(@NonNull Status status, @Nullable T data, @Nullable String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }

        public static <T> ViewState<T> idle() {
            return new ViewState<>(Status.IDLE, null, null);
        }
        public static <T> ViewState<T> loading(String msg) {
            return new ViewState<>(Status.LOADING, null, msg);
        }
        public static <T> ViewState<T> loaded(T data) {
            return new ViewState<>(Status.LOADED, data, null);
        }
        public static <T> ViewState<T> saving() {
            return new ViewState<>(Status.SAVING, null, null);
        }
        public static <T> ViewState<T> success(T data) {
            return new ViewState<>(Status.SUCCESS, data, null);
        }
        public static <T> ViewState<T> error(String message) {
            return new ViewState<>(Status.ERROR, null, message);
        }
    }

    private final VoucherRepository repository;

    private final MutableLiveData<ViewState<List<VoucherResponse>>> _listState = new MutableLiveData<>(ViewState.idle());
    public final LiveData<ViewState<List<VoucherResponse>>> listState = _listState;
    private Call<List<VoucherResponse>> listInFlight;


    private final MutableLiveData<ViewState<VoucherResponse>> _detailState = new MutableLiveData<>(ViewState.idle());
    public final LiveData<ViewState<VoucherResponse>> detailState = _detailState;
    private VoucherResponse loadedVoucher;
    private Call<VoucherResponse> detailInFlight;


    private final MutableLiveData<ViewState<VoucherResponse>> _saveState = new MutableLiveData<>(null); // null = "đã dọn dẹp"
    public final LiveData<ViewState<VoucherResponse>> saveState = _saveState;
    private Call<VoucherResponse> saveInFlight;


    public VoucherViewModel() {
        this.repository = new VoucherRepository();
    }


    public void loadList(String code, String status, String type, String sortBy) {
        if (listInFlight != null) listInFlight.cancel();
        _listState.postValue(ViewState.loading("Đang tải..."));

        listInFlight = repository.list(code, status, type, sortBy);
        listInFlight.enqueue(new Callback<List<VoucherResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<VoucherResponse>> call, @NonNull Response<List<VoucherResponse>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    _listState.postValue(ViewState.loaded(resp.body()));
                } else {
                    _listState.postValue(ViewState.error("Lỗi tải danh sách: " + resp.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<VoucherResponse>> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                _listState.postValue(ViewState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }


    public void createVoucher(VoucherRequest request) {
        if (saveInFlight != null) saveInFlight.cancel();

        ValidationResult validation = VoucherValidator.validate(request);

        if (!validation.isValid) {
            String errorMessage = "Lỗi " + validation.errorField + ": " + validation.errorMessage;
            _saveState.postValue(ViewState.error(errorMessage));
            return;
        }

        _saveState.postValue(ViewState.saving());

        saveInFlight = repository.create(request);

        saveInFlight.enqueue(new Callback<VoucherResponse>() {
            @Override
            public void onResponse(@NonNull Call<VoucherResponse> call, @NonNull Response<VoucherResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    _saveState.postValue(ViewState.success(resp.body()));
                } else {
                    _saveState.postValue(ViewState.error("Lỗi khi tạo: " + resp.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<VoucherResponse> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                _saveState.postValue(ViewState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public void loadVoucherDetail(String voucherId) {
        if (detailInFlight != null) detailInFlight.cancel();
        _detailState.postValue(ViewState.loading("Đang tải chi tiết..."));
        loadedVoucher = null; // Xóa cache cũ

        detailInFlight = repository.get(voucherId);
        detailInFlight.enqueue(new Callback<VoucherResponse>() {
            @Override
            public void onResponse(@NonNull Call<VoucherResponse> call, @NonNull Response<VoucherResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    loadedVoucher = resp.body(); // Lưu lại để tính diff
                    _detailState.postValue(ViewState.loaded(loadedVoucher));
                } else {
                    _detailState.postValue(ViewState.error("Lỗi tải chi tiết: " + resp.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<VoucherResponse> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                _detailState.postValue(ViewState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public void patchVoucher(String voucherId, VoucherRequest request) {
        if (loadedVoucher == null) {
            _saveState.postValue(ViewState.error("Lỗi: Không có dữ liệu gốc để so sánh."));
            return;
        }

        ValidationResult validation = VoucherValidator.validate(request);
        if (!validation.isValid) {
            String errorMessage = "Lỗi " + validation.errorField + ": " + validation.errorMessage;
            _saveState.postValue(ViewState.error(errorMessage));
            return;
        }

        if (saveInFlight != null) saveInFlight.cancel();
        _saveState.postValue(ViewState.saving());

        Map<String, Object> diff = calculateDiff(loadedVoucher, request);
        if (diff.isEmpty()) {
            _saveState.postValue(ViewState.success(loadedVoucher));
            return;
        }

        saveInFlight = repository.patch(voucherId, diff);
        saveInFlight.enqueue(new Callback<VoucherResponse>() {
            @Override
            public void onResponse(@NonNull Call<VoucherResponse> call, @NonNull Response<VoucherResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    _saveState.postValue(ViewState.success(resp.body()));
                } else {
                    _saveState.postValue(ViewState.error("Lỗi khi cập nhật: " + resp.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<VoucherResponse> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                _saveState.postValue(ViewState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public void clearSaveState() {
        _saveState.postValue(null);
    }

    public void clearDetailState() {
        _detailState.postValue(ViewState.idle());
        loadedVoucher = null;
    }

    private Map<String, Object> calculateDiff(VoucherResponse old, VoucherRequest req) {
        Map<String, Object> map = new HashMap<>();

        if (!Objects.equals(old.code, req.code)) map.put("code", req.code);
        if (!Objects.equals(old.type, req.type)) map.put("type", req.type);
        if (!Objects.equals(old.status, req.status)) map.put("status", req.status);
        if (!Objects.equals(old.startDate, req.startDate)) map.put("startDate", req.startDate);
        if (!Objects.equals(old.endDate, req.endDate)) map.put("endDate", req.endDate);

        // So sánh Double cần cẩn thận
        if (req.value != null && (old.value == null || old.value.doubleValue() != req.value)) {
            map.put("value", req.value);
        }

        return map;
    }

    @Override
    protected void onCleared() {
        // Hủy các cuộc gọi mạng đang chạy nếu ViewModel bị hủy
        if (listInFlight != null) listInFlight.cancel();
        if (detailInFlight != null) detailInFlight.cancel();
        if (saveInFlight != null) saveInFlight.cancel();
        super.onCleared();
    }
}
