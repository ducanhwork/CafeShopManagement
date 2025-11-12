package com.group3.application.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.util.Resource;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.BillCalculationResponse;
import com.group3.application.model.dto.BillGenerationRequest;
import com.group3.application.model.dto.BillResponse;
import com.group3.application.model.dto.CustomerSearchResponse;
import com.group3.application.model.dto.NewCustomerRequest;
import com.group3.application.model.dto.OrderUpdateDTO;
import com.group3.application.model.entity.Order;
import com.group3.application.model.repository.BillRepository;
import com.group3.application.model.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class GenerateBillViewModel extends AndroidViewModel {
    private final BillRepository billRepository;
    private final OrderRepository orderRepository;

    private final MutableLiveData<Order> order = new MutableLiveData<>();
    private final MutableLiveData<Resource<CustomerSearchResponse>> customerSearchResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<CustomerSearchResponse>> addCustomerResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<BillCalculationResponse>> calculateResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<BillResponse>> generateBillResult = new MutableLiveData<>();

    public GenerateBillViewModel(@NonNull Application application) {
        super(application);
        this.billRepository = new BillRepository();
        this.orderRepository = new OrderRepository(application);
    }

    public LiveData<Resource<CustomerSearchResponse>> getCustomerSearchResult() {
        return customerSearchResult;
    }

    public LiveData<Resource<CustomerSearchResponse>> getAddCustomerResult() {
        return addCustomerResult;
    }

    public LiveData<Resource<BillCalculationResponse>> getCalculateResult() {
        return calculateResult;
    }

    public LiveData<Resource<BillResponse>> getGenerateBillResult() {
        return generateBillResult;
    }

    public void setOrder(Order order) {
        this.order.setValue(order);
    }

    public void searchCustomer(String phone) {
        customerSearchResult.setValue(Resource.loading(null));
        billRepository.searchCustomer(phone).observeForever(resource -> customerSearchResult.setValue(resource));
    }

    public void addNewCustomer(String name, String phone) {
        addCustomerResult.setValue(Resource.loading(null));
        NewCustomerRequest request = new NewCustomerRequest(name, phone); // Assuming email is optional
        billRepository.addNewMember(request).observeForever(resource -> addCustomerResult.setValue(resource));
    }

    public void calculateBill(String customerPhone, String voucherCode, int points) {
        Order currentOrder = order.getValue();
        if (currentOrder == null) {
            calculateResult.setValue(Resource.error("Order not set", null));
            return;
        }

        calculateResult.setValue(Resource.loading(null));

        BillGenerationRequest request = new BillGenerationRequest(
            currentOrder.getId(),
            customerPhone,
            voucherCode,
            points
        );

        billRepository.calculateBill(request).observeForever(resource -> {
            calculateResult.setValue(resource);
        });
    }

    public void generateBill(String voucher, int points) {
        Order currentOrder = order.getValue();
        if (currentOrder == null) {
            generateBillResult.setValue(Resource.error("Chưa có thông tin đơn hàng", null));
            return;
        }

        Resource<CustomerSearchResponse> customerResource = customerSearchResult.getValue();
        String customerPhone = null;
        if (customerResource != null && customerResource.data != null) {
            customerPhone = customerResource.data.getPhone();
        }

        BillGenerationRequest billRequest = new BillGenerationRequest(
                currentOrder.getId(),
                customerPhone,
                voucher,
                points
        );

        generateBillResult.setValue(Resource.loading(null));
        billRepository.generateBill(billRequest).observeForever(billResource -> {
            if (billResource.status == Resource.Status.SUCCESS) {
                generateBillResult.postValue(billResource);
            } else if (billResource.status == Resource.Status.ERROR) {
                generateBillResult.postValue(Resource.error("Tạo hóa đơn thất bại: " + billResource.message, null));
            }
        });
    }

    private BigDecimal calculatePointsDiscount(int points) {
        return BigDecimal.valueOf(points * 1000L);
    }
}
