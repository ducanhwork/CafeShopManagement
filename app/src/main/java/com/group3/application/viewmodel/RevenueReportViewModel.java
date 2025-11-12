package com.group3.application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.dto.RevenueReportDTO;
import com.group3.application.model.repository.ReportRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RevenueReportViewModel extends AndroidViewModel {
    private final ReportRepository reportRepository;
    public final MutableLiveData<RevenueReportDTO> report = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();

    public final MutableLiveData<LocalDate> dateFrom = new MutableLiveData<>();
    public final MutableLiveData<LocalDate> dateTo = new MutableLiveData<>();
    public final MutableLiveData<String> filterBy = new MutableLiveData<>();
    private final DateTimeFormatter apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public RevenueReportViewModel(@NonNull Application application) {
        super(application);
        this.reportRepository = new ReportRepository(application);
        dateFrom.setValue(LocalDate.now());
        dateTo.setValue(LocalDate.now());
        filterBy.setValue("Day");
    }

    public void setDateFrom(LocalDate date) {
        dateFrom.setValue(date);
    }

    public void setDateTo(LocalDate date) {
        dateTo.setValue(date);
    }

    public void setFilterBy(String filter) {
        filterBy.setValue(filter);
        fetchReport();
    }

    public void fetchReport() {
        isLoading.setValue(true);

        LocalDateTime fromDateTime = dateFrom.getValue() != null ? dateFrom.getValue().atStartOfDay() : null;
        LocalDateTime toDateTime = dateTo.getValue() != null ? dateTo.getValue().atTime(23, 59, 59) : null;

        String from = fromDateTime != null ? fromDateTime.format(apiFormatter) : "";
        String to = toDateTime != null ? toDateTime.format(apiFormatter) : "";
        String filter = filterBy.getValue() != null ? filterBy.getValue().toUpperCase() : "DAY";

        reportRepository.getRevenueReport(from, to, filter, result -> {
            if (result.isSuccess()) {
                report.postValue(result.getData());
            } else {
                error.postValue(result.getMessage());
            }
            isLoading.postValue(false);
        });
    }
}
