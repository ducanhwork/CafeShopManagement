package com.group3.application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.util.Resource;
import com.group3.application.model.dto.BillSummaryDTO;
import com.group3.application.model.repository.BillRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillListViewModel extends AndroidViewModel {
    private final BillRepository repository;
    private final MutableLiveData<Resource<List<BillSummaryDTO>>> billList = new MutableLiveData<>();

    public BillListViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BillRepository();
    }

    public LiveData<Resource<List<BillSummaryDTO>>> getBillList() {
        return billList;
    }

    public void fetchBills(LocalDate date) {
        String dateString = (date != null) ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
        billList.setValue(Resource.loading(null));
        repository.getBillList(dateString).observeForever(billList::setValue);
    }
}
