package com.group3.application.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Bottom Sheet Dialog for starting a new shift.
 * Allows user to input opening cash amount with quick selection chips.
 * Follows Material Design BottomSheetDialogFragment pattern from Context7.
 */
public class StartShiftBottomSheet extends BottomSheetDialogFragment {

    private TextInputLayout tilOpeningCash;
    private TextInputEditText etOpeningCash;
    private MaterialButton btnCancel;
    private MaterialButton btnStart;
    private LinearProgressIndicator progressBar;
    private Chip chip500k, chip1m, chip2m, chip5m;

    private OnStartShiftListener listener;
    private final DecimalFormat decimalFormat;

    public StartShiftBottomSheet() {
        // Setup decimal format for Vietnamese currency (no decimal places)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    /**
     * Set the listener for shift start events
     */
    public void setOnStartShiftListener(OnStartShiftListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_start_shift, container, false);

        initializeViews(view);
        setupClickListeners();
        setupTextWatcher();

        return view;
    }

    private void initializeViews(View view) {
        tilOpeningCash = view.findViewById(R.id.til_opening_cash);
        etOpeningCash = view.findViewById(R.id.et_opening_cash);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnStart = view.findViewById(R.id.btn_start);
        progressBar = view.findViewById(R.id.progress_bar);
        chip500k = view.findViewById(R.id.chip_500k);
        chip1m = view.findViewById(R.id.chip_1m);
        chip2m = view.findViewById(R.id.chip_2m);
        chip5m = view.findViewById(R.id.chip_5m);
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());

        btnStart.setOnClickListener(v -> handleStartShift());

        // Quick amount chips
        chip500k.setOnClickListener(v -> setAmount("500000"));
        chip1m.setOnClickListener(v -> setAmount("1000000"));
        chip2m.setOnClickListener(v -> setAmount("2000000"));
        chip5m.setOnClickListener(v -> setAmount("5000000"));
    }

    private void setupTextWatcher() {
        etOpeningCash.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    etOpeningCash.removeTextChangedListener(this);

                    // Remove formatting characters
                    String cleanString = s.toString().replaceAll("[,.]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            long parsed = Long.parseLong(cleanString);
                            String formatted = decimalFormat.format(parsed);
                            current = formatted;
                            etOpeningCash.setText(formatted);
                            etOpeningCash.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                            // Invalid number, clear the field
                            etOpeningCash.setText("");
                        }
                    } else {
                        current = "";
                    }

                    etOpeningCash.addTextChangedListener(this);
                }
            }
        });
    }

    private void setAmount(String amount) {
        etOpeningCash.setText(amount);
        etOpeningCash.setSelection(amount.length());
    }

    private void handleStartShift() {
        String amountText = etOpeningCash.getText() != null ? 
                etOpeningCash.getText().toString() : "";

        if (amountText.isEmpty()) {
            tilOpeningCash.setError(getString(R.string.error_required_field));
            return;
        }

        // Parse amount
        String cleanAmount = amountText.replaceAll("[,.]", "");
        Double openingCash;
        try {
            openingCash = Double.parseDouble(cleanAmount);
        } catch (NumberFormatException e) {
            tilOpeningCash.setError(getString(R.string.error_invalid_amount));
            return;
        }

        // Validate amount (must be positive)
        if (openingCash <= 0) {
            tilOpeningCash.setError("Số tiền phải lớn hơn 0");
            return;
        }

        // Validate amount (reasonable max: 100 million)
        if (openingCash > 100000000.0) {
            tilOpeningCash.setError("Số tiền quá lớn (tối đa 100,000,000₫)");
            return;
        }

        tilOpeningCash.setError(null);

        // Show loading
        setLoading(true);

        // Notify listener
        if (listener != null) {
            listener.onStartShift(openingCash, this::onStartShiftComplete);
        }
    }

    private void onStartShiftComplete(boolean success, String message) {
        if (!isAdded()) return;

        setLoading(false);

        if (success) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            dismiss();
        } else {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnStart.setEnabled(!loading);
        btnCancel.setEnabled(!loading);
        etOpeningCash.setEnabled(!loading);
        chip500k.setEnabled(!loading);
        chip1m.setEnabled(!loading);
        chip2m.setEnabled(!loading);
        chip5m.setEnabled(!loading);
    }

    /**
     * Callback interface for shift start events
     */
    public interface OnStartShiftListener {
        void onStartShift(Double openingCash, StartShiftCallback callback);
    }

    /**
     * Callback for start shift completion
     */
    public interface StartShiftCallback {
        void onStartShiftComplete(boolean success, String message);
    }
}
