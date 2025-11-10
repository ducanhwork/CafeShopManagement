package com.group3.application.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.common.util.Resource;
import com.group3.application.model.dto.BillCalculationResponse;
import com.group3.application.model.dto.BillGenerationRequest;
import com.group3.application.model.dto.BillResponse;
import com.group3.application.model.dto.CustomerSearchResponse;
import com.group3.application.model.dto.NewCustomerRequest;
import com.group3.application.model.repository.BillRepository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ViewModel for Generate Bill Use Case (UC-0301)
 * Handles business logic and state management
 */
public class GenerateBillViewModel extends ViewModel {
    private final BillRepository repository;

    // Order data
    private final MutableLiveData<UUID> orderId = new MutableLiveData<>();

    // Customer data
    private final MutableLiveData<CustomerSearchResponse> customerData = new MutableLiveData<>();
    private final MutableLiveData<String> customerPhone = new MutableLiveData<>();

    // Discount data
    private final MutableLiveData<String> voucherCode = new MutableLiveData<>();
    private final MutableLiveData<Integer> pointsToRedeem = new MutableLiveData<>(0);

    // Calculation result
    private final MutableLiveData<BillCalculationResponse> calculationResult = new MutableLiveData<>();

    // API operation results
    private final MediatorLiveData<Resource<CustomerSearchResponse>> customerSearchResult = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<CustomerSearchResponse>> addMemberResult = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<BillCalculationResponse>> calculateResult = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<BillResponse>> generateBillResult = new MediatorLiveData<>();

    public GenerateBillViewModel() {
        this.repository = new BillRepository();
        pointsToRedeem.setValue(0);
    }

    // ========== Getters ==========
    public LiveData<UUID> getOrderId() { return orderId; }
    public LiveData<CustomerSearchResponse> getCustomerData() { return customerData; }
    public LiveData<String> getCustomerPhone() { return customerPhone; }
    public LiveData<String> getVoucherCode() { return voucherCode; }
    public LiveData<Integer> getPointsToRedeem() { return pointsToRedeem; }
    public LiveData<BillCalculationResponse> getCalculationResult() { return calculationResult; }

    public LiveData<Resource<CustomerSearchResponse>> getCustomerSearchResult() { return customerSearchResult; }
    public LiveData<Resource<CustomerSearchResponse>> getAddMemberResult() { return addMemberResult; }
    public LiveData<Resource<BillCalculationResponse>> getCalculateResult() { return calculateResult; }
    public LiveData<Resource<BillResponse>> getGenerateBillResult() { return generateBillResult; }

    // ========== Setters ==========
    public void setOrderId(UUID orderId) {
        this.orderId.setValue(orderId);
    }

    public void setCustomerPhone(String phone) {
        this.customerPhone.setValue(phone);
    }

    public void setVoucherCode(String code) {
        this.voucherCode.setValue(code);
    }

    public void setPointsToRedeem(int points) {
        this.pointsToRedeem.setValue(points);
    }

    // ========== UC-0301: Step 3-4 - Search Customer ==========
    public void searchCustomer(String phone) {
        LiveData<Resource<CustomerSearchResponse>> source = repository.searchCustomer(phone);
        customerSearchResult.addSource(source, resource -> {
            customerSearchResult.setValue(resource);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                customerData.setValue(resource.data);
                customerPhone.setValue(phone);
            }
            customerSearchResult.removeSource(source);
        });
    }

    // ========== AT1 - Add New Loyalty Member ==========
    public void addNewMember(String name, String phone, String email) {
        NewCustomerRequest request = new NewCustomerRequest(name, phone, email);
        LiveData<Resource<CustomerSearchResponse>> source = repository.addNewMember(request);
        addMemberResult.addSource(source, resource -> {
            addMemberResult.setValue(resource);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                customerData.setValue(resource.data);
                customerPhone.setValue(phone);
            }
            addMemberResult.removeSource(source);
        });
    }

    // ========== UC-0301: Step 7 - Calculate Bill ==========
    public void calculateBill() {
        if (orderId.getValue() == null) {
            calculateResult.setValue(Resource.error("Order ID is required", null));
            return;
        }

        BillGenerationRequest request = buildRequest();
        LiveData<Resource<BillCalculationResponse>> source = repository.calculateBill(request);
        calculateResult.addSource(source, resource -> {
            calculateResult.setValue(resource);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                calculationResult.setValue(resource.data);
            }
            calculateResult.removeSource(source);
        });
    }

    // ========== UC-0301: Step 8-10 - Generate Bill ==========
    public void generateBill() {
        if (orderId.getValue() == null) {
            generateBillResult.setValue(Resource.error("Order ID is required", null));
            return;
        }

        BillGenerationRequest request = buildRequest();
        LiveData<Resource<BillResponse>> source = repository.generateBill(request);
        generateBillResult.addSource(source, resource -> {
            generateBillResult.setValue(resource);
            generateBillResult.removeSource(source);
        });
    }

    // ========== Helper Methods ==========
    private BillGenerationRequest buildRequest() {
        return new BillGenerationRequest(
            orderId.getValue(),
            customerPhone.getValue(),
            voucherCode.getValue(),
            pointsToRedeem.getValue() != null ? pointsToRedeem.getValue() : 0
        );
    }

    /**
     * Validate points to redeem
     * @return true if valid, false otherwise
     */
    public boolean validatePoints(int points) {
        CustomerSearchResponse customer = customerData.getValue();
        if (customer == null) {
            return false;
        }
        return points <= customer.getAvailablePoints();
    }

    /**
     * Calculate discount from points (1 point = 1,000 VND)
     */
    public BigDecimal calculatePointsDiscount(int points) {
        return BigDecimal.valueOf(points * 1000);
    }

    /**
     * Check if customer is found
     */
    public boolean hasCustomer() {
        return customerData.getValue() != null;
    }

    /**
     * Get available points for current customer
     */
    public int getAvailablePoints() {
        CustomerSearchResponse customer = customerData.getValue();
        return customer != null ? customer.getAvailablePoints() : 0;
    }

    /**
     * Clear all data (for new bill generation)
     */
    public void clearData() {
        customerData.setValue(null);
        customerPhone.setValue(null);
        voucherCode.setValue(null);
        pointsToRedeem.setValue(0);
        calculationResult.setValue(null);
    }
}
