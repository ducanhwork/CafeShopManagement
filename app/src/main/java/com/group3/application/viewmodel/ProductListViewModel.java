package com.group3.application.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.common.utils.Event;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.CategoryDTO;
import com.group3.application.model.entity.Category;
import com.group3.application.model.entity.Product;
import com.group3.application.model.repository.CategoryRepository;
import com.group3.application.model.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductListViewModel extends AndroidViewModel {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final String TAG = "ProductListViewModel";
    private MutableLiveData<List<Product>> _products = new MutableLiveData<>();
    public final LiveData<List<Product>> products = _products;
    private MutableLiveData<List<CategoryDTO>> _categories = new MutableLiveData<>();
    public final LiveData<List<CategoryDTO>> categories = _categories;
    private MutableLiveData<Event<APIResult>> _updateStatusResult = new MutableLiveData<>();
    public LiveData<Event<APIResult>> updateStatusResult = _updateStatusResult;

    private MutableLiveData<Event<Product>> _navigateToProductDetail = new MutableLiveData<>();
    public LiveData<Event<Product>> navigateToProductDetail = _navigateToProductDetail;

    public ProductListViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        categoryRepository = new CategoryRepository(application);
    }

    public void getProducts(String keyword, String category) {
        productRepository.getProducts(result -> {
            if (result.isSuccess()) {
                _products.postValue((List<Product>) result.getData());
            } else {
                Log.e(TAG, "getProducts: " + result.getMessage());
            }
        }, keyword, category);
    }

    public void updateProductStatus(UUID productId, Boolean status) {
        productRepository.updateProductStatus(productId, status, result -> {
            _updateStatusResult.setValue(new Event<>(result));
            Log.d(TAG, "updateProductStatus: " + result.getMessage());

            // QUAN TRỌNG: Update local data khi API thành công
            if (result.isSuccess()) {
                updateLocalProductStatus(productId, status);
            }
        });
    }

    // Phương thức mới: cập nhật trạng thái sản phẩm trong local data
    private void updateLocalProductStatus(UUID productId, Boolean status) {
        List<Product> currentProducts = _products.getValue();
        if (currentProducts != null) {
            for (Product product : currentProducts) {
                if (product.getId().equals(productId)) {
                    product.setStatus(status ? "active" : "inactive");
                    break;
                }
            }
            // Post giá trị mới để LiveData thông báo thay đổi
            _products.postValue(new ArrayList<>(currentProducts));
        }
    }

    public void getCategories() {
        categoryRepository.getCategories(result -> {
            if (result.isSuccess()) {
                _categories.postValue((List<CategoryDTO>) result.getData());
            } else {
                Log.e(TAG, "getCategories: " + result.getMessage());
            }
        });
    }

}