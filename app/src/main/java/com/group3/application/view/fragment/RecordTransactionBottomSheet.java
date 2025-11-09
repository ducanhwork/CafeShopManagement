package com.group3.application.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Bottom Sheet Dialog for recording cash transactions during a shift
 * Allows user to record cash IN or cash OUT with amount and description
 */
public class RecordTransactionBottomSheet extends BottomSheetDialogFragment {

    private TextInputLayout tilTransactionType;
    private MaterialAutoCompleteTextView etTransactionType;
    private TextInputLayout tilAmount;
    private TextInputEditText etAmount;
    private TextInputLayout tilDescription;
    private TextInputEditText etDescription;
    private MaterialButton btnCancel;
    private MaterialButton btnRecord;
    private LinearProgressIndicator progressBar;

    private OnRecordTransactionListener listener;
    private final DecimalFormat decimalFormat;
    
    // Transaction types
    private static final String[] TRANSACTION_TYPES = {"CASH_IN", "CASH_OUT"};
    private static final String[] TRANSACTION_LABELS = {"Cash In (Thêm tiền)", "Cash Out (Rút tiền)"};

    public RecordTransactionBottomSheet() {
        // Setup decimal format for Vietnamese currency
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    /**
     * Set the listener for transaction record events
     */
    public void setOnRecordTransactionListener(OnRecordTransactionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_record_transaction, container, false);

        initializeViews(view);
        setupTransactionTypeDropdown();
        setupClickListeners();
        setupTextWatcher();

        return view;
    }

    private void initializeViews(View view) {
        tilTransactionType = view.findViewById(R.id.til_transaction_type);
        etTransactionType = view.findViewById(R.id.et_transaction_type);
        tilAmount = view.findViewById(R.id.til_amount);
        etAmount = view.findViewById(R.id.et_amount);
        tilDescription = view.findViewById(R.id.til_description);
        etDescription = view.findViewById(R.id.et_description);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnRecord = view.findViewById(R.id.btn_record);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupTransactionTypeDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                TRANSACTION_LABELS
        );
        etTransactionType.setAdapter(adapter);
        
        // Set default to CASH_IN
        etTransactionType.setText(TRANSACTION_LABELS[0], false);
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        btnRecord.setOnClickListener(v -> handleRecordTransaction());
    }

    private void setupTextWatcher() {
        etAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    etAmount.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[,.]", "");
                    
                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = decimalFormat.format(parsed);
                            current = formatted;
                            etAmount.setText(formatted);
                            etAmount.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                            // Ignore invalid input
                        }
                    }

                    etAmount.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleRecordTransaction() {
        // Clear previous errors
        tilTransactionType.setError(null);
        tilAmount.setError(null);

        // Validate inputs
        String typeLabel = etTransactionType.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim().replaceAll("[,.]", "");
        String description = etDescription.getText().toString().trim();

        if (typeLabel.isEmpty()) {
            tilTransactionType.setError("Please select transaction type");
            return;
        }

        if (amountStr.isEmpty()) {
            tilAmount.setError("Please enter amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                tilAmount.setError("Amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            tilAmount.setError("Invalid amount");
            return;
        }

        // Get transaction type from label
        String type = TRANSACTION_TYPES[0]; // Default CASH_IN
        for (int i = 0; i < TRANSACTION_LABELS.length; i++) {
            if (TRANSACTION_LABELS[i].equals(typeLabel)) {
                type = TRANSACTION_TYPES[i];
                break;
            }
        }

        // Show loading
        setLoading(true);

        // Call listener
        if (listener != null) {
            listener.onRecordTransaction(amount, type, description, new RecordTransactionCallback() {
                @Override
                public void onRecordComplete(boolean success, String message) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            setLoading(false);
                            if (success) {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRecord.setEnabled(!loading);
        btnCancel.setEnabled(!loading);
        etTransactionType.setEnabled(!loading);
        etAmount.setEnabled(!loading);
        etDescription.setEnabled(!loading);
    }

    /**
     * Listener interface for transaction record events
     */
    public interface OnRecordTransactionListener {
        void onRecordTransaction(double amount, String type, String description, RecordTransactionCallback callback);
    }

    /**
     * Callback interface for async transaction recording
     */
    public interface RecordTransactionCallback {
        void onRecordComplete(boolean success, String message);
    }
}
