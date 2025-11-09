/*package com.group3.application.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.model.entity.Category;
import com.group3.application.model.entity.Product;
import com.group3.application.model.repository.CategoryRepository;
import com.group3.application.model.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListViewModel extends ViewModel {
    private final ProductRepository productRepo = new ProductRepository();
    private final CategoryRepository categoryRepo = new CategoryRepository();

    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    private Call<List<Product>> inFlight;

    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<List<Category>> getCategories() {return categories;}
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }

    public void load(String status, String categoryId, String keyword) {
        if (inFlight != null) inFlight.cancel();
        loading.setValue(true); error.setValue(null);
        inFlight = productRepo.getProducts(status, categoryId, keyword);
        inFlight.enqueue(new Callback<List<Product>>() {
            @Override public void onResponse(Call<List<Product>> call, Response<List<Product>> resp) {
                loading.postValue(false);
                if (resp.isSuccessful() && resp.body()!=null) {
                    products.postValue(resp.body());
                } else {
                    error.postValue("Load failed: " + resp.code());
                }
            }
            @Override public void onFailure(Call<List<Product>> call, Throwable t) {
                if (call.isCanceled()) return;
                loading.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }


    public void fetchCategories() {
        categoryRepo.getCategories().enqueue(new Callback<List<Category>>() {
            @Override public void onResponse(Call<List<Category>> call, Response<List<Category>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) {
                    categories.postValue(resp.body());
                } else error.postValue("Load categories failed: " + resp.code());
            }
            @Override public void onFailure(Call<List<Category>> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }
    @Override
    protected void onCleared() {
        if (inFlight != null) inFlight.cancel();
    }
}*/
package com.group3.application.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3.application.model.entity.Category;
import com.group3.application.model.entity.Product;
import com.group3.application.model.repository.CategoryRepository;
import com.group3.application.model.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListViewModel extends ViewModel {

    private final ProductRepository productRepo = new ProductRepository();
    private final CategoryRepository categoryRepo = new CategoryRepository();

    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loadingProducts = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loadingCategories = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    private Call<List<Product>> inFlight;

    private String currentStatus = "active";
    private String currentCategoryId = null;
    private String currentKeyword = null;

    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<Boolean> getLoadingProducts() { return loadingProducts; }
    public LiveData<Boolean> getLoadingCategories() { return loadingCategories; }
    public LiveData<String> getError() { return error; }

    public void fetchCategories() {
        categoryRepo.getCategories().enqueue(new Callback<List<Category>>() {
            @Override public void onResponse(Call<List<Category>> call, Response<List<Category>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) {
                    categories.postValue(resp.body());
                } else error.postValue("Load categories failed: " + resp.code());
            }
            @Override public void onFailure(Call<List<Category>> call, Throwable t) {
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
        inFlight.enqueue(new Callback<List<Product>>() {
            @Override public void onResponse(Call<List<Product>> call, Response<List<Product>> resp) {
                loadingProducts.postValue(false);
                if (resp.isSuccessful() && resp.body()!=null) {
                    products.postValue(resp.body());
                } else error.postValue("Load failed: " + resp.code());
            }
            @Override public void onFailure(Call<List<Product>> call, Throwable t) {
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

