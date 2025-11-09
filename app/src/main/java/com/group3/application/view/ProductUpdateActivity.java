package com.group3.application.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.model.dto.ProductCreateRequest;
import com.group3.application.model.entity.Category;
import com.group3.application.viewmodel.ProductCreateViewModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductUpdateActivity extends AppCompatActivity {
    private static final String TAG = "NewProductActivity";

    private static final int PERMISSION_REQUEST_CODE_STORAGE = 101;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 102;

    private Toolbar toolbar;
    private LinearLayout layoutImageUploadArea;
    private LinearLayout layoutPlaceholderContent;
    private ImageView ivProductImage;

    private TextInputLayout tilProductName, tilProductPrice, tilProductDescription;
    private TextInputEditText etProductName, etProductPrice, etProductDescription;
    private Spinner spinnerCategory;
    private MaterialButton btnCreateProduct;

    private ImageView ivAvatar;

    private Uri currentImageUri = null;
    private String selectedCategory = null;

    // ViewModel
    private ProductCreateViewModel viewModel;

    // Activity result launchers
    private ActivityResultLauncher<Intent> galleryPickerLauncher;
    private ActivityResultLauncher<Uri> cameraCaptureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_update);

        initViews();
        viewModel = new ViewModelProvider(this).get(ProductCreateViewModel.class);
        setupToolbar();
        List<String> categoryNames = new ArrayList<>();
        viewModel.getCategories();
        viewModel.categories.observe(this, categories -> {
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }
        });
        setupCategorySpinner(categoryNames);
        setupImageUploadAreaListener();
        setupActivityResultLaunchers();

        setupObservers();

        setupCreateButtonListener();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        layoutImageUploadArea = findViewById(R.id.layout_image_upload_area);
        layoutPlaceholderContent = findViewById(R.id.layout_placeholder_content);
        ivProductImage = findViewById(R.id.iv_product_image);

        tilProductName = findViewById(R.id.til_product_name);
        tilProductPrice = findViewById(R.id.til_product_price);
        tilProductDescription = findViewById(R.id.til_product_description);

        etProductName = findViewById(R.id.et_product_name);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductDescription = findViewById(R.id.et_product_description);

        ivAvatar = findViewById(R.id.iv_avatar);
        spinnerCategory = findViewById(R.id.spinner_product_category);
        btnCreateProduct = findViewById(R.id.btn_create_product);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("New Product");
        }
    }

    private void setupCategorySpinner(List<String> categoryNames) {
        if (!categoryNames.contains("Select Category")) {
            categoryNames.add(0, "Select Category");
        }

        // Tạo ArrayAdapter, sử dụng layout cho item hiển thị
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, // <-- Sử dụng layout cho item hiển thị
                categoryNames);

        // Set layout cho các item trong danh sách thả xuống (dropdown)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // <-- Quan trọng

        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoryNames.get(position);
                Log.d(TAG, "Selected category: " + selectedCategory);
                // Cập nhật ViewModel hoặc làm gì đó với selectedCategoryName
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
                Log.d(TAG, "Nothing selected in category spinner.");
            }
        });

        // Đặt mục mặc định là "Select Category" nếu cần
        if (selectedCategory == null && !categoryNames.isEmpty()) {
            spinnerCategory.setSelection(0); // Chọn mục đầu tiên (Select Category)
        }
    }

    private void setupImageUploadAreaListener() {
        layoutImageUploadArea.setOnClickListener(v -> {
            showImageSourceDialog();
        });
    }

    private void setupActivityResultLaunchers() {
        galleryPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri selectedUri = result.getData().getData();
                if (selectedUri != null) {
                    currentImageUri = selectedUri; // Lưu URI hiện tại
                    displayImage(currentImageUri); // Hiển thị ảnh
                }
            }
        });

        cameraCaptureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success) {
                if (currentImageUri != null) {
                    displayImage(currentImageUri); // Hiển thị ảnh
                }
            } else {
                Log.d(TAG, "Camera capture cancelled or failed.");
                // Xử lý lỗi nếu cần, có thể xóa file tạm
            }
        });
    }

    private void showImageSourceDialog() {
        new MaterialAlertDialogBuilder(this).setTitle("Choose Image Source").setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
            switch (which) {
                case 0: // Gallery
                    if (checkStoragePermission()) {
                        openGallery();
                    } else {
                        requestStoragePermission();
                    }
                    break;
                case 1: // Camera
                    if (checkCameraPermission()) {
                        takePhoto();
                    } else {
                        requestCameraPermission();
                    }
                    break;
            }
        }).show();
    }
    public void bindData(List<String> categoryNames){
        Intent intent = getIntent();
        if (intent != null){
        String productName = intent.getStringExtra("productName");
        String productPrice = intent.getStringExtra("productPrice");
        String productDescription = intent.getStringExtra("productDescription");
        String productCategory = intent.getStringExtra("productCategory");
        String productImage = intent.getStringExtra("productImage");

        etProductName.setText(productName);
        etProductPrice.setText(productPrice);
        etProductDescription.setText(productDescription);
        spinnerCategory.setSelection(categoryNames.indexOf(productCategory));
        displayImage(Uri.parse(productImage));
        }

    }
    private boolean checkStoragePermission() { /* ... */
        return true;
    } // Placeholder

    private void requestStoragePermission() { /* ... */ } // Placeholder

    private boolean checkCameraPermission() { /* ... */
        return true;
    }

    private void requestCameraPermission() { /* ... */ } // Placeholder

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // ... logic xử lý quyền y như cũ ...
    }
// --- End Permission Handling ---

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryPickerLauncher.launch(intent);
    }

    private void takePhoto() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
            if (photoFile != null) {
                currentImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraCaptureLauncher.launch(currentImageUri);
            } else {
                Toast.makeText(this, "Could not prepare camera.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException ex) {
            Log.e(TAG, "Error creating image file", ex);
            Toast.makeText(this, "Could not create temporary file for photo.", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "FileProvider error", e);
            Toast.makeText(this, "Could not prepare camera (FileProvider error).", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            throw new IOException("External storage directory not available.");
        }
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return imageFile;
    }

    // Hàm này chỉ cập nhật UI, logic gọi API sẽ nằm ở ViewModel
    private void displayImage(Uri imageUri) {
        if (imageUri != null) {
            ivProductImage.setImageURI(imageUri);
            ivProductImage.setVisibility(View.VISIBLE);
            layoutPlaceholderContent.setVisibility(View.GONE);
        } else {
            ivProductImage.setImageDrawable(null);
            ivProductImage.setVisibility(View.GONE);
            layoutPlaceholderContent.setVisibility(View.VISIBLE);
        }
    }

    // --- ViewModel Observers ---
    private void setupObservers() {
        // Quan sát trạng thái loading
        viewModel.isLoading.observe(this, isLoading -> {
            if (isLoading) {
                showLoading(true); // Hiển thị progress bar hoặc disable button
            } else {
                showLoading(false); // Ẩn progress bar hoặc enable button
            }
        });

        // Quan sát kết quả tạo sản phẩm thành công
        viewModel.productCreated.observe(this, productResponse -> {
            if (productResponse != null) {
                Toast.makeText(this, "Product created: " + productResponse.getName(), Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish(); // Đóng Activity hiện tại sau khi thành công
            }
        });

        // Quan sát lỗi
        viewModel.errorMessage.observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Helper method để hiển thị/ẩn loading
    private void showLoading(boolean show) {
        if (show) {
            btnCreateProduct.setEnabled(false); // Tắt nút
            btnCreateProduct.setText("Creating..."); // Thay đổi text
            // Có thể hiển thị ProgressBar ở đây
        } else {
            btnCreateProduct.setEnabled(true); // Bật lại nút
            btnCreateProduct.setText("Create Product"); // Reset text
            // Có thể ẩn ProgressBar
        }
    }

    // --- Validation and Product Creation Trigger ---
    private void setupCreateButtonListener() {
        btnCreateProduct.setOnClickListener(v -> {
            if (validateInput()) {
                createProduct(); // Kích hoạt quy trình tạo sản phẩm
            }
        });
    }

    private boolean validateInput() {
        tilProductName.setError(null);
        tilProductPrice.setError(null);
        tilProductDescription.setError(null);

        String name = etProductName.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        boolean isValid = true;

        if (name.isEmpty()) {
            tilProductName.setError("Product name is required");
            isValid = false;
        }

        if (priceStr.isEmpty()) {
            tilProductPrice.setError("Product price is required");
            isValid = false;
        } else {
            try {
                // Kiểm tra giá hợp lệ
                Double price = Double.parseDouble(priceStr);
                if (price < 0) {
                    tilProductPrice.setError("Price cannot be negative");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilProductPrice.setError("Invalid price format");
                isValid = false;
            }
        }

        if (description.isEmpty()) {
            tilProductDescription.setError("Product description is required");
            isValid = false;
        }

        if (category.equals("Select Category")) {
            Toast.makeText(this, "Please select a product category", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Kiểm tra xem ảnh đã được chọn/chụp và hiển thị chưa
        if (ivProductImage.getVisibility() == View.GONE) {
            Toast.makeText(this, "Please add a product image", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    // Hàm này chỉ chuẩn bị dữ liệu và gọi ViewModel
    private void createProduct() {
        String name = etProductName.getText().toString().trim();
        Double price = Double.parseDouble(etProductPrice.getText().toString().trim());
        String description = etProductDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        ProductCreateRequest productData = new ProductCreateRequest(name, description, price, category);


        // Gọi hàm trong ViewModel để thực hiện API call
        viewModel.createProduct(productData, currentImageUri);
    }

    // Handle toolbar back button click (Giữ nguyên như cũ)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (hasUnsavedChanges()) {
                showUnsavedChangesDialog();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasUnsavedChanges() { /* ... */
        return false;
    } // Placeholder

    // Show dialog to confirm leaving if there are unsaved changes (Giữ nguyên như cũ)
    private void showUnsavedChangesDialog() { /* ... */ } // Placeholder
}