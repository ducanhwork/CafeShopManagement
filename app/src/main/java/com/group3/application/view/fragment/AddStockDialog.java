package com.group3.application.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.viewmodel.ImageUtils;

/**
 * Dialog fragment for adding stock transactions (Incoming, Outgoing, Adjustment)
 * Features:
 * - Transaction type selection with radio buttons
 * - Quantity input with validation
 * - Quick add buttons (10, 50, 100, 500)
 * - Real-time preview of new stock level
 * - Notes field with character counter
 * - Material Design 3 styling
 */
public class AddStockDialog extends DialogFragment {

    private static final String ARG_INGREDIENT = "ingredient";
    private static final String TAG = "AddStockDialog";

    // UI Components
    private ImageView ivIngredientImage;
    private TextView tvIngredientName;
    private TextView tvCurrentStock;
    private RadioGroup rgTransactionType;
    private TextInputLayout tilQuantity;
    private EditText etQuantity;
    private TextInputLayout tilNotes;
    private EditText etNotes;
    private MaterialCardView cvNewStockPreview;
    private TextView tvCurrentStockValue;
    private TextView tvNewStockValue;
    private Button btnQuick10, btnQuick50, btnQuick100, btnQuick500;
    private Button btnCancel, btnConfirm;

    // Data
    private Ingredient ingredient;
    private OnStockAddedListener listener;

    /**
     * Interface for handling stock addition events
     */
    public interface OnStockAddedListener {
        /**
         * Called when stock transaction is confirmed
         * 
         * @param ingredientId The ingredient ID
         * @param quantity The quantity to add (positive for incoming, negative for outgoing)
         * @param type Transaction type (INCOMING, OUTGOING, ADJUSTMENT)
         * @param notes Optional notes for the transaction
         */
        void onStockAdded(int ingredientId, double quantity, String type, String notes);
    }

    /**
     * Create new instance of AddStockDialog
     * 
     * @param ingredient The ingredient to add stock for
     * @return New dialog instance
     */
    public static AddStockDialog newInstance(@NonNull Ingredient ingredient) {
        AddStockDialog dialog = new AddStockDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INGREDIENT, ingredient);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Set listener for stock addition events
     */
    public void setOnStockAddedListener(OnStockAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Retrieve ingredient from arguments
        if (getArguments() != null) {
            ingredient = (Ingredient) getArguments().getSerializable(ARG_INGREDIENT);
        }

        if (ingredient == null) {
            throw new IllegalStateException("Ingredient must be provided");
        }

        // Inflate dialog layout
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_stock, null);

        // Initialize views
        initializeViews(view);

        // Setup UI
        setupIngredientInfo();
        setupQuickButtons();
        setupQuantityInput();

        // Create dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setView(view);

        AlertDialog dialog = builder.create();

        // Setup button actions
        btnCancel.setOnClickListener(v -> dismiss());
        btnConfirm.setOnClickListener(v -> confirmStockAddition());

        return dialog;
    }

    /**
     * Initialize all view references
     */
    private void initializeViews(View view) {
        ivIngredientImage = view.findViewById(R.id.ivIngredientImage);
        tvIngredientName = view.findViewById(R.id.tvIngredientName);
        tvCurrentStock = view.findViewById(R.id.tvCurrentStock);
        rgTransactionType = view.findViewById(R.id.rgTransactionType);
        tilQuantity = view.findViewById(R.id.tilQuantity);
        etQuantity = view.findViewById(R.id.etQuantity);
        tilNotes = view.findViewById(R.id.tilNotes);
        etNotes = view.findViewById(R.id.etNotes);
        cvNewStockPreview = view.findViewById(R.id.cvNewStockPreview);
        tvCurrentStockValue = view.findViewById(R.id.tvCurrentStockValue);
        tvNewStockValue = view.findViewById(R.id.tvNewStockValue);
        btnQuick10 = view.findViewById(R.id.btnQuick10);
        btnQuick50 = view.findViewById(R.id.btnQuick50);
        btnQuick100 = view.findViewById(R.id.btnQuick100);
        btnQuick500 = view.findViewById(R.id.btnQuick500);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnConfirm = view.findViewById(R.id.btnConfirm);
    }

    /**
     * Setup ingredient information display
     */
    private void setupIngredientInfo() {
        // Load ingredient image
        ImageUtils.loadIngredientThumbnail(
                requireContext(),
                ingredient.getImageLink(),
                ivIngredientImage
        );

        // Set ingredient name
        tvIngredientName.setText(ingredient.getName());

        // Set current stock
        String stockText = getString(R.string.current_stock_format, ingredient.getQuantityInStock());
        tvCurrentStock.setText(stockText);
        tvCurrentStockValue.setText(String.valueOf(ingredient.getQuantityInStock()));
    }

    /**
     * Setup quick add buttons
     */
    private void setupQuickButtons() {
        btnQuick10.setOnClickListener(v -> addQuickQuantity(10));
        btnQuick50.setOnClickListener(v -> addQuickQuantity(50));
        btnQuick100.setOnClickListener(v -> addQuickQuantity(100));
        btnQuick500.setOnClickListener(v -> addQuickQuantity(500));

        // Also listen for transaction type changes to update preview
        rgTransactionType.setOnCheckedChangeListener((group, checkedId) -> updateStockPreview());
    }

    /**
     * Add quick quantity to input field
     */
    private void addQuickQuantity(int amount) {
        String currentText = etQuantity.getText().toString().trim();
        double currentValue = 0;
        
        if (!currentText.isEmpty()) {
            try {
                currentValue = Double.parseDouble(currentText);
            } catch (NumberFormatException e) {
                currentValue = 0;
            }
        }

        double newValue = currentValue + amount;
        etQuantity.setText(String.valueOf(newValue));
        etQuantity.setSelection(etQuantity.getText().length());
    }

    /**
     * Setup quantity input with real-time preview
     */
    private void setupQuantityInput() {
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStockPreview();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Update the stock level preview based on quantity and transaction type
     */
    private void updateStockPreview() {
        String quantityText = etQuantity.getText().toString().trim();
        
        if (quantityText.isEmpty()) {
            cvNewStockPreview.setVisibility(View.GONE);
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityText);
            if (quantity <= 0) {
                cvNewStockPreview.setVisibility(View.GONE);
                return;
            }

            // Get transaction type
            String type = getSelectedTransactionType();
            
            // Calculate new stock level
            double currentStock = ingredient.getQuantityInStock();
            double newStock;

            switch (type) {
                case "INCOMING":
                    newStock = currentStock + quantity;
                    break;
                case "OUTGOING":
                    newStock = currentStock - quantity;
                    break;
                case "ADJUSTMENT":
                    // For adjustment, quantity can be positive or negative
                    // We'll treat it as a direct adjustment
                    newStock = currentStock + quantity;
                    break;
                default:
                    newStock = currentStock;
            }

            // Update preview
            tvNewStockValue.setText(String.format("%.2f", newStock));
            cvNewStockPreview.setVisibility(View.VISIBLE);

            // Clear error if any
            tilQuantity.setError(null);

        } catch (NumberFormatException e) {
            cvNewStockPreview.setVisibility(View.GONE);
        }
    }

    /**
     * Get selected transaction type
     */
    private String getSelectedTransactionType() {
        int selectedId = rgTransactionType.getCheckedRadioButtonId();
        
        if (selectedId == R.id.rbIncoming) {
            return "INCOMING";
        } else if (selectedId == R.id.rbOutgoing) {
            return "OUTGOING";
        } else if (selectedId == R.id.rbAdjustment) {
            return "ADJUSTMENT";
        }
        
        return "INCOMING"; // Default
    }

    /**
     * Validate and confirm stock addition
     */
    private void confirmStockAddition() {
        // Validate quantity
        String quantityText = etQuantity.getText().toString().trim();
        
        if (quantityText.isEmpty()) {
            tilQuantity.setError(getString(R.string.error_quantity_required));
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            tilQuantity.setError(getString(R.string.error_invalid_quantity));
            return;
        }

        if (quantity <= 0) {
            tilQuantity.setError(getString(R.string.error_quantity_must_be_positive));
            return;
        }

        // Get transaction type
        String type = getSelectedTransactionType();

        // Check if outgoing quantity exceeds current stock
        if ("OUTGOING".equals(type)) {
            if (quantity > ingredient.getQuantityInStock()) {
                tilQuantity.setError(getString(R.string.error_quantity_exceeds_stock));
                return;
            }
        }

        // Get notes (optional)
        String notes = etNotes.getText().toString().trim();

        // Adjust quantity based on transaction type
        double adjustedQuantity = quantity;
        if ("OUTGOING".equals(type)) {
            adjustedQuantity = -quantity; // Negative for outgoing
        }

        // Notify listener
        if (listener != null) {
            listener.onStockAdded(ingredient.getId(), adjustedQuantity, type, notes);
        }

        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear listener to prevent memory leaks
        listener = null;
    }
}
