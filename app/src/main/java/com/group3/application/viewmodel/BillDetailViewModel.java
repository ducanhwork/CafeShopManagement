package com.group3.application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.util.Resource;
import com.group3.application.model.dto.BillDetailResponse;
import com.group3.application.model.dto.BillResponse;
import com.group3.application.model.dto.PaymentConfirmationResponse;
import com.group3.application.model.repository.BillRepository;

public class BillDetailViewModel extends AndroidViewModel {

    private final BillRepository repository;
    private final MutableLiveData<Resource<BillDetailResponse>> billDetails = new MutableLiveData<>();
    private final MutableLiveData<Resource<PaymentConfirmationResponse>> paymentResult = new MutableLiveData<>();

    private String currentBillId;

    public BillDetailViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BillRepository();
    }

    public LiveData<Resource<BillDetailResponse>> getBillDetails() {
        return billDetails;
    }

    public LiveData<Resource<PaymentConfirmationResponse>> getPaymentResult() {
        return paymentResult;
    }

    public void loadBillDetails(String billId) {
        this.currentBillId = billId;
        billDetails.setValue(Resource.loading(null));
        repository.getBillDetails(billId).observeForever(billResource -> billDetails.setValue(billResource));
    }

    public void confirmPayment(String paymentMethod) {
        if (currentBillId == null) {
            paymentResult.setValue(Resource.error("Bill ID is missing", null));
            return;
        }
        paymentResult.setValue(Resource.loading(null));
        repository.confirmPayment(currentBillId, paymentMethod).observeForever(result -> paymentResult.setValue(result));
    }
}
