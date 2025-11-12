package com.group3.application.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.StockReportDTO;
import com.group3.application.model.repository.ReportRepository;

public class StockReportViewModel extends AndroidViewModel {

    private ReportRepository reportRepository;

    public final MutableLiveData<StockReportDTO> report = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public final MutableLiveData<String> error = new MutableLiveData<>();

    public StockReportViewModel(@NonNull Application application) {
        super(application);
        reportRepository = new ReportRepository(application);
    }

    public void fetchReport() {
        isLoading.setValue(true);
        reportRepository.getStockReport(result -> {
            isLoading.setValue(false);
            if (result.isSuccess()) {
                report.setValue(result.getData());
            } else {
                error.setValue(result.getMessage());
            }
        });
    }
}