package com.group3.application.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.group3.application.model.dto.ProductCreateRequest;
import com.group3.application.model.dto.ProductUpdateRequest;
import com.group3.application.model.entity.Category;
import com.group3.application.model.entity.Product;
import com.group3.application.model.repository.CategoryRepository;
import com.group3.application.model.repository.ProductRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductUpdateViewModel extends AndroidViewModel {
    private MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    public final LiveData<List<Category>> categories = _categories;
    private static final String TAG = "ProductCreateViewModel";

    // Repository để tương tác với data source (API)
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    // LiveData để theo dõi trạng thái API call
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<Product> _productUpdated = new MutableLiveData<>();
    public LiveData<Product> productUpdated = _productUpdated;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    // Constructor
    public ProductUpdateViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        productRepository = new ProductRepository(application); // Pass application context if needed by repository
    }

    // Hàm gọi API để tạo sản phẩm

    public void updateProduct(
            UUID productId,
            ProductUpdateRequest productData,
            Uri imageUri
    ) {
//        if (imageUri == null) {
//            _errorMessage.setValue("Product image is required.");
//            return;
//        }


        _isLoading.setValue(true);
        _errorMessage.setValue(null);

        // Chuẩn bị RequestBody cho product data và MultipartBody.Part cho image
        RequestBody productRequestBody = convertProductToJsonRequestBody(productData);
        MultipartBody.Part imagePart = null;

        if (imageUri != null) {

            imagePart = convertImageUriToMultipart(imageUri);

            if (productRequestBody == null || imagePart == null) {
                _errorMessage.setValue("Failed to prepare data for upload.");
                _isLoading.setValue(false);
                return;
            }
        }

        productRepository.updateProduct(productId, productRequestBody, imagePart, new Callback<Product>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                _isLoading.setValue(false); // Kết thúc loading
                if (response.isSuccessful() && response.body() != null) {
                    // Thành công
                    _productUpdated.setValue(response.body());
                } else {
                    // Thất bại
                    String errorMsg = "Failed to update product.";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                            Log.e(TAG, "API Error Response: " + errorMsg);
                        } else {
                            errorMsg += " (" + response.code() + ")";
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    _errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                _isLoading.setValue(false); // Kết thúc loading
                Log.e(TAG, "API Failure: " + t.getMessage(), t);
                _errorMessage.setValue("Network error. Please try again.");
            }
        });
    }

    public void getCategories() {
        categoryRepository.getCategories(result -> {
            if (result.isSuccess()) {
                _categories.postValue((List<Category>) result.getData());
            } else {
                Log.e(TAG, "getCategories: " + result.getMessage());
            }
        });
    }

    // Helper method: Chuyển ProductCreateRequest sang RequestBody (JSON)
    private RequestBody convertProductToJsonRequestBody(ProductUpdateRequest data) {
        try {
            Gson gson = new Gson(); // Yêu cầu dependency 'com.google.code.gson:gson:2.x.x'
            String jsonString = gson.toJson(data);
            return RequestBody.create(jsonString, okhttp3.MediaType.parse("application/json"));
        } catch (Exception e) {
            Log.e(TAG, "Error converting product data to JSON", e);
            return null;
        }
    }

    // Helper method: Chuyển Uri ảnh sang MultipartBody.Part
    private MultipartBody.Part convertImageUriToMultipart(Uri imageUri) {
        try {
            String fileName = "product_image_" + System.currentTimeMillis() + ".jpg"; // Tên file động
            InputStream inputStream = getApplication().getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Log.e(TAG, "Input stream for image URI is null.");
                return null;
            }
            byte[] imageBytes = getBytes(inputStream); // Sử dụng helper getBytes
            inputStream.close();

            RequestBody imageRequestBody = RequestBody.create(imageBytes, okhttp3.MediaType.parse("image/jpeg")); // Hoặc "image/png"
            return MultipartBody.Part.createFormData("image", fileName, imageRequestBody); // "image" phải khớp với key server
        } catch (IOException e) {
            Log.e(TAG, "Error converting image URI to MultipartBody.Part", e);
            return null;
        }
    }

    // Helper method: InputStream to byte array (same as provided before)
    private byte[] getBytes(InputStream inputStream) throws IOException {
        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            try {
                byteBuffer.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing ByteArrayOutputStream", e);
            }
        }
        return bytesResult;
    }
}