package com.group3.application.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;

/**
 * BottomSheet dialog for adding a new table
 * Provides form inputs for table name, seat count, location, and status
 */
public class AddTableBottomSheet extends BottomSheetDialogFragment {

    private TextInputEditText etTableName;
    private TextInputEditText etSeatCount;
    private AutoCompleteTextView etLocation;
    private AutoCompleteTextView actvStatus;
    private TextInputLayout tilTableName;
    private TextInputLayout tilSeatCount;
    private TextInputLayout tilLocation;
    private MaterialButton btnCancel;
    private MaterialButton btnCreate;
    private LinearProgressIndicator progressBar;

    private OnTableCreateListener listener;

    private static final String[] STATUS_OPTIONS = {"Available", "Occupied", "Reserved"};
    private static final String[] LOCATION_OPTIONS = {"Indoor", "Outdoor", "Balcony"};

    public interface OnTableCreateListener {
        void onTableCreate(String name, int seatCount, String location, String status);
    }

    public static AddTableBottomSheet newInstance(OnTableCreateListener listener) {
        AddTableBottomSheet fragment = new AddTableBottomSheet();
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        etTableName = view.findViewById(R.id.etTableName);
        etSeatCount = view.findViewById(R.id.etSeatCount);
        etLocation = view.findViewById(R.id.etLocation);
        actvStatus = view.findViewById(R.id.actvStatus);
        tilTableName = view.findViewById(R.id.tilTableName);
        tilSeatCount = view.findViewById(R.id.tilSeatCount);
        tilLocation = view.findViewById(R.id.tilLocation);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCreate = view.findViewById(R.id.btnCreate);
        progressBar = view.findViewById(R.id.progressBar);

        // Setup status dropdown
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                STATUS_OPTIONS
        );
        actvStatus.setAdapter(statusAdapter);
        actvStatus.setText(STATUS_OPTIONS[0], false); // Default to "Available"

        // Setup location dropdown
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                LOCATION_OPTIONS
        );
        etLocation.setAdapter(locationAdapter);
        etLocation.setText(LOCATION_OPTIONS[0], false); // Default to "Indoor"

        // Setup button listeners
        btnCancel.setOnClickListener(v -> dismiss());
        btnCreate.setOnClickListener(v -> validateAndCreate());
    }

    private void validateAndCreate() {
        // Clear previous errors
        tilTableName.setError(null);
        tilSeatCount.setError(null);
        tilLocation.setError(null);

        // Get input values
        String name = etTableName.getText() != null ? etTableName.getText().toString().trim() : "";
        String seatCountStr = etSeatCount.getText() != null ? etSeatCount.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
        String status = actvStatus.getText().toString();

        // Validate inputs
        boolean hasError = false;

        if (TextUtils.isEmpty(name)) {
            tilTableName.setError("Table name is required");
            hasError = true;
        } else if (name.length() < 2) {
            tilTableName.setError("Table name must be at least 2 characters");
            hasError = true;
        }

        if (TextUtils.isEmpty(seatCountStr)) {
            tilSeatCount.setError("Seat count is required");
            hasError = true;
        } else {
            try {
                int seatCount = Integer.parseInt(seatCountStr);
                if (seatCount < 1) {
                    tilSeatCount.setError("Seat count must be at least 1");
                    hasError = true;
                } else if (seatCount > 50) {
                    tilSeatCount.setError("Seat count cannot exceed 50");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                tilSeatCount.setError("Please enter a valid number");
                hasError = true;
            }
        }

        if (TextUtils.isEmpty(location)) {
            tilLocation.setError("Location is required");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // All validations passed, create table
        int seatCount = Integer.parseInt(seatCountStr);
        
        // Show loading
        setLoading(true);

        if (listener != null) {
            listener.onTableCreate(name, seatCount, location, status);
        }
    }

    /**
     * Show/hide loading state
     */
    public void setLoading(boolean loading) {
        if (progressBar == null || !isAdded()) return;
        
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCreate.setEnabled(!loading);
        btnCancel.setEnabled(!loading);
        etTableName.setEnabled(!loading);
        etSeatCount.setEnabled(!loading);
        etLocation.setEnabled(!loading);
        actvStatus.setEnabled(!loading);
    }

    /**
     * Show error message
     */
    public void showError(String message) {
        if (!isAdded()) return;
        setLoading(false);
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Close dialog on success
     */
    public void onSuccess() {
        if (!isAdded()) return;
        setLoading(false);
        Toast.makeText(requireContext(), "Table created successfully!", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
