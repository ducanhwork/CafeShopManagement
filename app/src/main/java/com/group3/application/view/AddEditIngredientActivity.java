package com.group3.application.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.model.bean.CreateIngredientRequest;
import com.group3.application.model.bean.UpdateIngredientRequest;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.viewmodel.ImageUtils;
import com.group3.application.viewmodel.InventoryViewModel;

/**
 * Activity for adding or editing ingredients
 * Features:
 * - Create new ingredients with all fields
 * - Edit existing ingredients
 * - Image URL input with preview
 * - Validation for all fields
 * - Status toggle (active/inactive)
 * - Delete functionality for existing ingredients
 * - Manager-only access
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";
    public static final String EXTRA_MODE = "mode";
    public static final String MODE_ADD = "add";
    public static final String MODE_EDIT = "edit";

    // UI Components
    private MaterialToolbar toolbar;
    private ImageView ivIngredientPreview;
    private TextInputLayout tilImageUrl, tilName, tilDescription, tilPrice;
    private TextInputLayout tilInitialStock, tilReorderLevel;
    private EditText etImageUrl, etName, etDescription, etPrice;
    private EditText etInitialStock, etReorderLevel;
    private SwitchMaterial switchStatus;
    private Button btnLoadImage, btnSave, btnDelete;
    private FrameLayout loadingOverlay;

    // Data
    private InventoryViewModel viewModel;
    private String mode;
    private int ingredientId = -1;
    private Ingredient currentIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // Get mode and ingredient ID from intent
        mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) {
            mode = MODE_ADD;
        }

        if (MODE_EDIT.equals(mode)) {
            ingredientId = getIntent().getIntExtra(EXTRA_INGREDIENT_ID, -1);
            if (ingredientId == -1) {
                Snackbar.make(findViewById(android.R.id.content),
                        R.string.error_invalid_ingredient, Snackbar.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Setup UI based on mode
        setupMode();

        // Setup listeners
        setupListeners();

        // Observe ViewModel
        observeViewModel();

        // Load ingredient if in edit mode
        if (MODE_EDIT.equals(mode)) {
            viewModel.getIngredientById(ingredientId);
        }
    }

    /**
     * Initialize all view references
     */
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        ivIngredientPreview = findViewById(R.id.ivIngredientPreview);
        tilImageUrl = findViewById(R.id.tilImageUrl);
        tilName = findViewById(R.id.tilName);
        tilDescription = findViewById(R.id.tilDescription);
        tilPrice = findViewById(R.id.tilPrice);
        tilInitialStock = findViewById(R.id.tilInitialStock);
        tilReorderLevel = findViewById(R.id.tilReorderLevel);
        etImageUrl = findViewById(R.id.etImageUrl);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etInitialStock = findViewById(R.id.etInitialStock);
        etReorderLevel = findViewById(R.id.etReorderLevel);
        switchStatus = findViewById(R.id.switchStatus);
        btnLoadImage = findViewById(R.id.btnLoadImage);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    /**
     * Setup toolbar with back button
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Setup UI based on add or edit mode
     */
    private void setupMode() {
        if (MODE_ADD.equals(mode)) {
            toolbar.setTitle(R.string.add_ingredient);
            tilInitialStock.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
        } else {
            toolbar.setTitle(R.string.edit_ingredient);
            tilInitialStock.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Setup click listeners and text watchers
     */
    private void setupListeners() {
        // Load image button
        btnLoadImage.setOnClickListener(v -> loadImagePreview());

        // Image URL text watcher for auto-loading
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://"))) {
                    loadImagePreview();
                }
            }
        });

        // Save button
        btnSave.setOnClickListener(v -> saveIngredient());

        // Delete button
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    /**
     * Observe ViewModel LiveData
     */
    private void observeViewModel() {
        // Current ingredient (for edit mode)
        viewModel.getCurrentIngredient().observe(this, ingredient -> {
            if (ingredient != null) {
                currentIngredient = ingredient;
                populateFields(ingredient);
            }
        });

        // Loading state
        viewModel.getLoading().observe(this, isLoading -> {
            loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSave.setEnabled(!isLoading);
            btnDelete.setEnabled(!isLoading);
        });

        // Error messages
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });

        // Success messages
        viewModel.getSuccessMessage().observe(this, success -> {
            if (success != null && !success.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), success, Snackbar.LENGTH_SHORT).show();
                // Close activity after successful save/delete
                finish();
            }
        });
    }

    /**
     * Load image preview from URL
     */
    private void loadImagePreview() {
        String imageUrl = etImageUrl.getText().toString().trim();
        
        if (imageUrl.isEmpty()) {
            ivIngredientPreview.setImageResource(R.drawable.ic_ingredient_placeholder);
            return;
        }

        ImageUtils.loadIngredientImageFull(this, imageUrl, ivIngredientPreview);
    }

    /**
     * Populate fields with ingredient data (edit mode)
     */
    private void populateFields(Ingredient ingredient) {
        etName.setText(ingredient.getName());
        etDescription.setText(ingredient.getDescription());
        etPrice.setText(String.valueOf(ingredient.getPrice()));
        etReorderLevel.setText(String.valueOf(ingredient.getReorderLevel()));
        etImageUrl.setText(ingredient.getImageLink());
        switchStatus.setChecked("active".equals(ingredient.getStatus()));

        // Load image
        if (ingredient.getImageLink() != null && !ingredient.getImageLink().isEmpty()) {
            loadImagePreview();
        }
    }

    /**
     * Validate all input fields
     */
    private boolean validateFields() {
        boolean isValid = true;

        // Clear previous errors
        tilName.setError(null);
        tilPrice.setError(null);
        tilInitialStock.setError(null);
        tilReorderLevel.setError(null);

        // Validate name
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_empty_name));
            isValid = false;
        } else if (name.length() > 100) {
            tilName.setError(getString(R.string.error_name_too_long));
            isValid = false;
        }

        // Validate description
        String description = etDescription.getText().toString().trim();
        if (description.length() > 500) {
            tilDescription.setError(getString(R.string.error_description_too_long));
            isValid = false;
        }

        // Validate price
        String priceStr = etPrice.getText().toString().trim();
        if (priceStr.isEmpty()) {
            tilPrice.setError(getString(R.string.error_invalid_price));
            isValid = false;
        } else {
            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    tilPrice.setError(getString(R.string.error_invalid_price));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilPrice.setError(getString(R.string.error_invalid_price));
                isValid = false;
            }
        }

        // Validate initial stock (only for add mode)
        if (MODE_ADD.equals(mode)) {
            String initialStockStr = etInitialStock.getText().toString().trim();
            if (initialStockStr.isEmpty()) {
                tilInitialStock.setError(getString(R.string.error_invalid_quantity));
                isValid = false;
            } else {
                try {
                    double initialStock = Double.parseDouble(initialStockStr);
                    if (initialStock < 0) {
                        tilInitialStock.setError(getString(R.string.error_invalid_quantity));
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    tilInitialStock.setError(getString(R.string.error_invalid_quantity));
                    isValid = false;
                }
            }
        }

        // Validate reorder level
        String reorderLevelStr = etReorderLevel.getText().toString().trim();
        if (reorderLevelStr.isEmpty()) {
            tilReorderLevel.setError(getString(R.string.error_invalid_reorder_level));
            isValid = false;
        } else {
            try {
                double reorderLevel = Double.parseDouble(reorderLevelStr);
                if (reorderLevel < 0) {
                    tilReorderLevel.setError(getString(R.string.error_invalid_reorder_level));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilReorderLevel.setError(getString(R.string.error_invalid_reorder_level));
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Save ingredient (create or update)
     */
    private void saveIngredient() {
        if (!validateFields()) {
            return;
        }

        // Get values
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        double price = Double.parseDouble(etPrice.getText().toString().trim());
        double reorderLevel = Double.parseDouble(etReorderLevel.getText().toString().trim());
        String imageUrl = etImageUrl.getText().toString().trim();
        String status = switchStatus.isChecked() ? "active" : "inactive";

        if (MODE_ADD.equals(mode)) {
            // Create new ingredient
            double initialStock = Double.parseDouble(etInitialStock.getText().toString().trim());
            
            CreateIngredientRequest request = new CreateIngredientRequest();
            request.setName(name);
            request.setDescription(description);
            request.setPrice(price);
            request.setImageLink(imageUrl);
            request.setStatus(status);
            request.setCategoryName("Ingredient");
            request.setQuantityInStock(initialStock);
            request.setReorderLevel(reorderLevel);

            viewModel.createIngredient(request);
        } else {
            // Update existing ingredient
            UpdateIngredientRequest request = new UpdateIngredientRequest();
            request.setName(name);
            request.setDescription(description);
            request.setPrice(price);
            request.setImageLink(imageUrl);
            request.setStatus(status);
            request.setReorderLevel(reorderLevel);

            viewModel.updateIngredient(ingredientId, request);
        }
    }

    /**
     * Show delete confirmation dialog
     */
    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_ingredient)
                .setMessage(R.string.delete_ingredient_confirm)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteIngredient(ingredientId);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Check if there are unsaved changes
        if (hasUnsavedChanges()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.unsaved_changes)
                    .setMessage(R.string.unsaved_changes_message)
                    .setPositiveButton(R.string.discard, (dialog, which) -> super.onBackPressed())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Check if there are unsaved changes
     */
    private boolean hasUnsavedChanges() {
        if (MODE_ADD.equals(mode)) {
            // In add mode, check if any field has been filled
            return !etName.getText().toString().trim().isEmpty() ||
                   !etDescription.getText().toString().trim().isEmpty() ||
                   !etPrice.getText().toString().trim().isEmpty() ||
                   !etImageUrl.getText().toString().trim().isEmpty();
        } else {
            // In edit mode, check if any field has been changed
            if (currentIngredient == null) {
                return false;
            }

            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String reorderLevelStr = etReorderLevel.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();
            boolean isActive = switchStatus.isChecked();

            double price = priceStr.isEmpty() ? 0 : Double.parseDouble(priceStr);
            double reorderLevel = reorderLevelStr.isEmpty() ? 0 : Double.parseDouble(reorderLevelStr);

            return !name.equals(currentIngredient.getName()) ||
                   !description.equals(currentIngredient.getDescription()) ||
                   price != currentIngredient.getPrice() ||
                   reorderLevel != currentIngredient.getReorderLevel() ||
                   !imageUrl.equals(currentIngredient.getImageLink()) ||
                   isActive != "active".equals(currentIngredient.getStatus());
        }
    }
}
