package com.group3.application.viewmodel; // (Sửa package)

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.PeriodItemReportDTO;
import com.group3.application.model.repository.ReportRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ItemReportViewModel extends AndroidViewModel {

    private ReportRepository reportRepository;
    public final MutableLiveData<List<PeriodItemReportDTO>> report = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public final MutableLiveData<String> error = new MutableLiveData<>();

    public final MutableLiveData<LocalDate> dateFrom = new MutableLiveData<>();
    public final MutableLiveData<LocalDate> dateTo = new MutableLiveData<>();
    public final MutableLiveData<String> filterBy = new MutableLiveData<>("DAY");

    private final DateTimeFormatter apiFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ItemReportViewModel(@NonNull Application application) {
        super(application);
        reportRepository = new ReportRepository(application);

        LocalDate now = LocalDate.now();
        dateFrom.setValue(now.withDayOfMonth(1));
        dateTo.setValue(now);
    }

    public void fetchReport() {
        isLoading.setValue(true);

        LocalDateTime from = dateFrom.getValue().atStartOfDay();
        LocalDateTime to = dateTo.getValue().atTime(LocalTime.MAX);

        reportRepository.getItemReport(
                from.format(apiFormatter),
                to.format(apiFormatter),
                filterBy.getValue(),
                result -> {
                    isLoading.setValue(false);
                    if (result.isSuccess()) {
                        report.setValue(result.getData());
                    } else {
                        error.setValue(result.getMessage());
                    }
                }
        );
    }

    public void setDateFrom(LocalDate date) {
        dateFrom.setValue(date);
    }
    public void setDateTo(LocalDate date) {
        dateTo.setValue(date);
    }
    public void setFilterBy(String filter) {
        filterBy.setValue(filter);
    }
}