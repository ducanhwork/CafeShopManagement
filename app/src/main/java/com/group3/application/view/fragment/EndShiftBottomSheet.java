package com.group3.application.view.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Bottom Sheet Dialog for ending a shift.
 * Shows opening/expected cash and calculates discrepancy with color coding:
 * - Green: Perfect match (0 discrepancy)
 * - Yellow: Minor discrepancy (< 50,000₫)
 * - Red: Major discrepancy (>= 50,000₫)
 * 
 * Follows Material Design BottomSheetDialogFragment pattern from Context7.
 */
public class EndShiftBottomSheet extends BottomSheetDialogFragment {

    private TextView tvOpeningCash;
    private TextView tvExpectedCash;
    private TextInputLayout tilClosingCash;
    private TextInputEditText etClosingCash;
    private MaterialCardView cardDiscrepancy;
    private TextView tvDiscrepancyAmount;
    private TextView tvDiscrepancyMessage;
    private MaterialButton btnCancel;
    private MaterialButton btnEnd;
    private LinearProgressIndicator progressBar;

    private BigDecimal openingCash;
    private BigDecimal expectedCash;
    private OnEndShiftListener listener;

    private final DecimalFormat decimalFormat;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public EndShiftBottomSheet() {
        // Setup decimal format for input formatting
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    /**
     * Set shift data (opening and expected cash)
     */
    public void setShiftData(BigDecimal openingCash, BigDecimal expectedCash) {
        this.openingCash = openingCash;
        this.expectedCash = expectedCash;
    }

    /**
     * Set the listener for shift end events
     */
    public void setOnEndShiftListener(OnEndShiftListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_end_shift, container, false);

        initializeViews(view);
        populateShiftData();
        setupClickListeners();
        setupTextWatcher();

        return view;
    }

    private void initializeViews(View view) {
        tvOpeningCash = view.findViewById(R.id.tv_opening_cash);
        tvExpectedCash = view.findViewById(R.id.tv_expected_cash);
        tilClosingCash = view.findViewById(R.id.til_closing_cash);
        etClosingCash = view.findViewById(R.id.et_closing_cash);
        cardDiscrepancy = view.findViewById(R.id.card_discrepancy);
        tvDiscrepancyAmount = view.findViewById(R.id.tv_discrepancy_amount);
        tvDiscrepancyMessage = view.findViewById(R.id.tv_discrepancy_message);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnEnd = view.findViewById(R.id.btn_end);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void populateShiftData() {
        if (openingCash != null) {
            tvOpeningCash.setText(currencyFormat.format(openingCash));
        }
        if (expectedCash != null) {
            tvExpectedCash.setText(currencyFormat.format(expectedCash));
            // Pre-fill with expected amount
            String expectedStr = expectedCash.toPlainString().replaceAll("\\.0+$", "");
            etClosingCash.setText(expectedStr);
            etClosingCash.setSelection(expectedStr.length());
        }
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        btnEnd.setOnClickListener(v -> handleEndShift());
    }

    private void setupTextWatcher() {
        etClosingCash.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    etClosingCash.removeTextChangedListener(this);

                    // Remove formatting characters
                    String cleanString = s.toString().replaceAll("[,.]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            long parsed = Long.parseLong(cleanString);
                            String formatted = decimalFormat.format(parsed);
                            current = formatted;
                            etClosingCash.setText(formatted);
                            etClosingCash.setSelection(formatted.length());

                            // Calculate and show discrepancy
                            calculateDiscrepancy(new BigDecimal(parsed));
                        } catch (NumberFormatException e) {
                            etClosingCash.setText("");
                            cardDiscrepancy.setVisibility(View.GONE);
                        }
                    } else {
                        current = "";
                        cardDiscrepancy.setVisibility(View.GONE);
                    }

                    etClosingCash.addTextChangedListener(this);
                }
            }
        });
    }

    private void calculateDiscrepancy(BigDecimal closingCash) {
        if (expectedCash == null) return;

        BigDecimal discrepancy = closingCash.subtract(expectedCash);
        BigDecimal absDiscrepancy = discrepancy.abs();

        // Show discrepancy card
        cardDiscrepancy.setVisibility(View.VISIBLE);

        // Format discrepancy with sign
        String sign = discrepancy.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        tvDiscrepancyAmount.setText(sign + currencyFormat.format(discrepancy));

        // Determine discrepancy level and apply color coding
        int level = getDiscrepancyLevel(absDiscrepancy);
        applyDiscrepancyColors(level, discrepancy);

        // Set message based on discrepancy
        if (discrepancy.compareTo(BigDecimal.ZERO) == 0) {
            tvDiscrepancyMessage.setText("Khớp chính xác! ✓");
        } else if (discrepancy.compareTo(BigDecimal.ZERO) > 0) {
            tvDiscrepancyMessage.setText("Dư tiền so với dự kiến");
        } else {
            tvDiscrepancyMessage.setText("Thiếu tiền so với dự kiến");
        }
    }

    private int getDiscrepancyLevel(BigDecimal absDiscrepancy) {
        if (absDiscrepancy.compareTo(BigDecimal.ZERO) == 0) {
            return 0; // Perfect - green
        } else if (absDiscrepancy.compareTo(new BigDecimal("50000")) < 0) {
            return 1; // Minor - yellow
        } else {
            return 2; // Major - red
        }
    }

    private void applyDiscrepancyColors(int level, BigDecimal discrepancy) {
        int backgroundColor;
        int textColor;

        switch (level) {
            case 0: // Perfect - Green
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.md_theme_successContainer);
                textColor = ContextCompat.getColor(requireContext(), R.color.md_theme_onSuccessContainer);
                break;
            case 1: // Minor - Yellow
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.md_theme_warningContainer);
                textColor = ContextCompat.getColor(requireContext(), R.color.md_theme_onWarningContainer);
                break;
            case 2: // Major - Red
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer);
                textColor = ContextCompat.getColor(requireContext(), R.color.md_theme_onErrorContainer);
                break;
            default:
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.md_theme_surfaceVariant);
                textColor = ContextCompat.getColor(requireContext(), R.color.md_theme_onSurfaceVariant);
        }

        cardDiscrepancy.setCardBackgroundColor(ColorStateList.valueOf(backgroundColor));
        tvDiscrepancyAmount.setTextColor(textColor);
        tvDiscrepancyMessage.setTextColor(textColor);
    }

    private void handleEndShift() {
        String amountText = etClosingCash.getText() != null ? 
                etClosingCash.getText().toString() : "";

        if (amountText.isEmpty()) {
            tilClosingCash.setError(getString(R.string.error_required_field));
            return;
        }

        // Parse amount
        String cleanAmount = amountText.replaceAll("[,.]", "");
        BigDecimal closingCash;
        try {
            closingCash = new BigDecimal(cleanAmount);
        } catch (NumberFormatException e) {
            tilClosingCash.setError(getString(R.string.error_invalid_amount));
            return;
        }

        // Validate amount (must be non-negative)
        if (closingCash.compareTo(BigDecimal.ZERO) < 0) {
            tilClosingCash.setError("Số tiền không thể âm");
            return;
        }

        tilClosingCash.setError(null);

        // Show loading
        setLoading(true);

        // Notify listener
        if (listener != null) {
            listener.onEndShift(closingCash, this::onEndShiftComplete);
        }
    }

    private void onEndShiftComplete(boolean success, String message) {
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
        btnEnd.setEnabled(!loading);
        btnCancel.setEnabled(!loading);
        etClosingCash.setEnabled(!loading);
    }

    /**
     * Callback interface for shift end events
     */
    public interface OnEndShiftListener {
        void onEndShift(BigDecimal closingCash, EndShiftCallback callback);
    }

    /**
     * Callback for end shift completion
     */
    public interface EndShiftCallback {
        void onEndShiftComplete(boolean success, String message);
    }
}
