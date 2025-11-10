package com.group3.application.model.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.group3.application.common.util.Resource;
import com.group3.application.model.dto.BillCalculationResponse;
import com.group3.application.model.dto.BillGenerationRequest;
import com.group3.application.model.dto.BillResponse;
import com.group3.application.model.dto.CustomerSearchResponse;
import com.group3.application.model.dto.NewCustomerRequest;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Bill operations
 * Handles data operations and abstracts API calls from ViewModel
 */
public class BillRepository {
    private final ApiService apiService;

    public BillRepository() {
        this.apiService = ApiClient.getApi();
    }

    /**
     * Search for customer by phone number
     */
    public LiveData<Resource<CustomerSearchResponse>> searchCustomer(String phone) {
        MutableLiveData<Resource<CustomerSearchResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.searchCustomer(phone).enqueue(new Callback<CustomerSearchResponse>() {
            @Override
            public void onResponse(Call<CustomerSearchResponse> call, Response<CustomerSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Customer not found", null));
                }
            }

            @Override
            public void onFailure(Call<CustomerSearchResponse> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Add new loyalty member
     */
    public LiveData<Resource<CustomerSearchResponse>> addNewMember(NewCustomerRequest request) {
        MutableLiveData<Resource<CustomerSearchResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.addNewMember(request).enqueue(new Callback<CustomerSearchResponse>() {
            @Override
            public void onResponse(Call<CustomerSearchResponse> call, Response<CustomerSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to create new member", null));
                }
            }

            @Override
            public void onFailure(Call<CustomerSearchResponse> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Calculate bill with discounts
     */
    public LiveData<Resource<BillCalculationResponse>> calculateBill(BillGenerationRequest request) {
        MutableLiveData<Resource<BillCalculationResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.calculateBill(request).enqueue(new Callback<BillCalculationResponse>() {
            @Override
            public void onResponse(Call<BillCalculationResponse> call, Response<BillCalculationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    String errorMsg = "Calculation failed";
                    if (response.code() == 400) {
                        errorMsg = "Invalid voucher or insufficient points";
                    }
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<BillCalculationResponse> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Generate bill (final step)
     */
    public LiveData<Resource<BillResponse>> generateBill(BillGenerationRequest request) {
        MutableLiveData<Resource<BillResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.generateBill(request).enqueue(new Callback<BillResponse>() {
            @Override
            public void onResponse(Call<BillResponse> call, Response<BillResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to generate bill", null));
                }
            }

            @Override
            public void onFailure(Call<BillResponse> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }
}
