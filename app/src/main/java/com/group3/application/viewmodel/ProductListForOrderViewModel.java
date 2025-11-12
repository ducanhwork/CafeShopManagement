package com.group3.application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.dto.CategoryDTO;
import com.group3.application.model.dto.ProductForOrder;
import com.group3.application.model.repository.CategoryRepository;
import com.group3.application.model.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListForOrderViewModel extends AndroidViewModel {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductListForOrderViewModel(
        @NonNull Application application
        ) {
        super(application);
        this.productRepo = new ProductRepository(application);
        this.categoryRepo = new CategoryRepository(application);
    }

    private final MutableLiveData<List<CategoryDTO>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<ProductForOrder>> products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loadingProducts = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loadingCategories = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    private Call<List<ProductForOrder>> inFlight;

    private String currentStatus = "active";
    private String currentCategoryId = null;
    private String currentKeyword = null;

    public LiveData<List<CategoryDTO>> getCategories() { return categories; }
    public LiveData<List<ProductForOrder>> getProducts() { return products; }
    public LiveData<Boolean> getLoadingProducts() { return loadingProducts; }
    public LiveData<Boolean> getLoadingCategories() { return loadingCategories; }
    public LiveData<String> getError() { return error; }

    public void fetchCategories() {
        categoryRepo.getCategories().enqueue(new Callback<List<CategoryDTO>>() {
            @Override public void onResponse(Call<List<CategoryDTO>> call, Response<List<CategoryDTO>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) {
                    categories.postValue(resp.body());
                } else error.postValue("Load categories failed: " + resp.code());
            }
            @Override public void onFailure(Call<List<CategoryDTO>> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    public void applyFilter(String categoryIdOrNull, String keywordOrNull) {
        currentCategoryId = (categoryIdOrNull == null || categoryIdOrNull.isBlank()) ? null : categoryIdOrNull;
        currentKeyword = (keywordOrNull == null || keywordOrNull.isBlank()) ? null : keywordOrNull;
        reload();
    }

    public void setStatus(String statusOrNull){
        currentStatus = (statusOrNull == null || statusOrNull.isBlank()) ? null : statusOrNull;
        reload();
    }

    public void reload() {
        if (inFlight != null) inFlight.cancel();
        loadingProducts.setValue(true);
        error.setValue(null);

        inFlight = productRepo.getProducts(currentStatus, currentCategoryId, currentKeyword);
        inFlight.enqueue(new Callback<List<ProductForOrder>>() {
            @Override public void onResponse(Call<List<ProductForOrder>> call, Response<List<ProductForOrder>> resp) {
                loadingProducts.postValue(false);
                if (resp.isSuccessful() && resp.body()!=null) {
                    products.postValue(resp.body());
                } else error.postValue("Load failed: " + resp.code());
            }
            @Override public void onFailure(Call<List<ProductForOrder>> call, Throwable t) {
                if (call.isCanceled()) return;
                loadingProducts.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }

    @Override protected void onCleared() {
        if (inFlight != null) inFlight.cancel();
    }
}
