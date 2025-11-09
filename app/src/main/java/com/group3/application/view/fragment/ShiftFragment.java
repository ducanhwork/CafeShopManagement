package com.group3.application.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.group3.application.R;
import com.group3.application.model.entity.CashBalance;
import com.group3.application.model.entity.Shift;
import com.group3.application.viewmodel.ShiftViewModel;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Shift Management Fragment
 * Displays current shift status, cash balance, and shift operations.
 * Uses MVVM pattern with ShiftViewModel and LiveData.
 */
public class ShiftFragment extends Fragment {

    private ShiftViewModel viewModel;

    // UI Components
    private SwipeRefreshLayout swipeRefresh;
    private Chip chipShiftStatus;
    private LinearLayout layoutShiftInfo;
    private LinearLayout layoutCashBalance;
    private LinearLayout layoutEmptyState;
    private MaterialCardView cardTransactionSummary;
    private TextView tvShiftStartTime;
    private TextView tvShiftDuration;
    private TextView tvShiftOpeningCash;
    private TextView tvCurrentBalance;
    private TextView tvExpectedCash;
    private TextView tvTransactionCount;
    private MaterialButton btnStartShift;
    private MaterialButton btnEndShift;
    private MaterialButton btnRecordTransaction;
    private CircularProgressIndicator progressBar;

    // Formatters
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shift, container, false);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ShiftViewModel.class);

        // Initialize Views
        initializeViews(view);

        // Setup Observers
        setupObservers();

        // Setup Click Listeners
        setupClickListeners();

        // Load current shift
        viewModel.loadCurrentShift();

        return view;
    }

    private void initializeViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        chipShiftStatus = view.findViewById(R.id.chip_shift_status);
        layoutShiftInfo = view.findViewById(R.id.layout_shift_info);
        layoutCashBalance = view.findViewById(R.id.layout_cash_balance);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        cardTransactionSummary = view.findViewById(R.id.card_transaction_summary);
        tvShiftStartTime = view.findViewById(R.id.tv_shift_start_time);
        tvShiftDuration = view.findViewById(R.id.tv_shift_duration);
        tvShiftOpeningCash = view.findViewById(R.id.tv_shift_opening_cash);
        tvCurrentBalance = view.findViewById(R.id.tv_current_balance);
        tvExpectedCash = view.findViewById(R.id.tv_expected_cash);
        tvTransactionCount = view.findViewById(R.id.tv_transaction_count);
        btnStartShift = view.findViewById(R.id.btn_start_shift);
        btnEndShift = view.findViewById(R.id.btn_end_shift);
        btnRecordTransaction = view.findViewById(R.id.btn_record_transaction);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupObservers() {
        // Observe current shift
        viewModel.getCurrentShift().observe(getViewLifecycleOwner(), this::updateShiftUI);

        // Observe cash balance
        viewModel.getCashBalance().observe(getViewLifecycleOwner(), this::updateCashBalanceUI);

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            swipeRefresh.setRefreshing(false);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty()) {
                Toast.makeText(requireContext(), success, Toast.LENGTH_SHORT).show();
                viewModel.clearSuccess();
            }
        });

        // Observe shift status
        viewModel.getHasOpenShift().observe(getViewLifecycleOwner(), this::updateButtonsVisibility);
    }

    private void setupClickListeners() {
        // Swipe to refresh
        swipeRefresh.setOnRefreshListener(() -> viewModel.refreshData());

        // Start Shift button
        btnStartShift.setOnClickListener(v -> showStartShiftDialog());

        // End Shift button
        btnEndShift.setOnClickListener(v -> showEndShiftDialog());

        // Record Transaction button
        btnRecordTransaction.setOnClickListener(v -> showRecordTransactionDialog());
    }

    private void showRecordTransactionDialog() {
        RecordTransactionBottomSheet bottomSheet = new RecordTransactionBottomSheet();
        bottomSheet.setOnRecordTransactionListener((amount, type, description, callback) -> {
            // referenceNumber can be null or auto-generated
            viewModel.recordTransaction(amount, type, description, null);
            
            // Observe the result
            viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
                if (success != null && !success.isEmpty()) {
                    callback.onRecordComplete(true, success);
                    viewModel.clearSuccess();
                }
            });
            
            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
                if (error != null && !error.isEmpty()) {
                    callback.onRecordComplete(false, error);
                    viewModel.clearError();
                }
            });
        });
        bottomSheet.show(getParentFragmentManager(), "RecordTransactionBottomSheet");
    }

    private void showStartShiftDialog() {
        StartShiftBottomSheet bottomSheet = new StartShiftBottomSheet();
        bottomSheet.setOnStartShiftListener((openingCash, callback) -> {
            viewModel.startShift(openingCash);
            
            // Observe the result
            viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
                if (success != null && !success.isEmpty()) {
                    callback.onStartShiftComplete(true, success);
                }
            });
            
            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
                if (error != null && !error.isEmpty()) {
                    callback.onStartShiftComplete(false, error);
                }
            });
        });
        bottomSheet.show(getParentFragmentManager(), "StartShiftBottomSheet");
    }

    private void showEndShiftDialog() {
        Shift currentShift = viewModel.getCurrentShift().getValue();
        CashBalance cashBalance = viewModel.getCashBalance().getValue();
        
        if (currentShift == null || cashBalance == null) {
            Toast.makeText(requireContext(), "Không thể kết thúc ca: Thiếu thông tin ca làm việc", Toast.LENGTH_SHORT).show();
            return;
        }

        EndShiftBottomSheet bottomSheet = new EndShiftBottomSheet();
        bottomSheet.setShiftData(currentShift.getOpeningCash(), cashBalance.getExpectedBalance());
        bottomSheet.setOnEndShiftListener((closingCash, callback) -> {
            viewModel.endShift(closingCash);
            
            // Observe the result
            viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
                if (success != null && !success.isEmpty()) {
                    callback.onEndShiftComplete(true, success);
                }
            });
            
            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
                if (error != null && !error.isEmpty()) {
                    callback.onEndShiftComplete(false, error);
                }
            });
        });
        bottomSheet.show(getParentFragmentManager(), "EndShiftBottomSheet");
    }

    private void updateShiftUI(Shift shift) {
        if (shift == null) {
            // No active shift
            chipShiftStatus.setText(R.string.shift_no_active);
            chipShiftStatus.setChipBackgroundColorResource(android.R.color.darker_gray);
            layoutShiftInfo.setVisibility(View.GONE);
            layoutCashBalance.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            cardTransactionSummary.setVisibility(View.GONE);
        } else {
            // Active shift
            if (shift.isOpen()) {
                chipShiftStatus.setText(R.string.shift_open);
                chipShiftStatus.setChipBackgroundColorResource(android.R.color.holo_green_dark);
            } else {
                chipShiftStatus.setText(R.string.shift_closed);
                chipShiftStatus.setChipBackgroundColorResource(android.R.color.holo_red_dark);
            }

            // Show shift info
            layoutShiftInfo.setVisibility(View.VISIBLE);
            layoutCashBalance.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);

            // Update shift details
            try {
                Date startDate = isoFormat.parse(shift.getStartTime());
                tvShiftStartTime.setText(String.format("Bắt đầu: %s", 
                        startDate != null ? dateTimeFormat.format(startDate) : shift.getStartTime()));
            } catch (ParseException e) {
                tvShiftStartTime.setText(String.format("Bắt đầu: %s", shift.getStartTime()));
            }

            // Duration (only if shift has duration field)
            if (shift.getDurationMinutes() != null && shift.getDurationMinutes() > 0) {
                tvShiftDuration.setText(String.format("Thời gian: %s", shift.getFormattedDuration()));
                tvShiftDuration.setVisibility(View.VISIBLE);
            } else {
                tvShiftDuration.setVisibility(View.GONE);
            }

            // Opening cash
            tvShiftOpeningCash.setText(String.format("Tiền mở ca: %s", 
                    formatCurrency(shift.getOpeningCash())));
        }
    }

    private void updateCashBalanceUI(CashBalance balance) {
        if (balance == null) {
            layoutCashBalance.setVisibility(View.GONE);
            cardTransactionSummary.setVisibility(View.GONE);
            return;
        }

        layoutCashBalance.setVisibility(View.VISIBLE);
        
        // Calculate current balance from expected balance and display it
        // Current balance = expectedBalance (which is opening + cashIn - cashOut - refunds)
        if (balance.getExpectedBalance() != null) {
            tvCurrentBalance.setText(formatCurrency(balance.getExpectedBalance()));
        } else {
            tvCurrentBalance.setText(formatCurrency(0.0));
        }
        
        tvExpectedCash.setText(formatCurrency(balance.getExpectedBalance()));
        
        if (balance.getTransactionCount() != null) {
            tvTransactionCount.setText(String.valueOf(balance.getTransactionCount()));
            cardTransactionSummary.setVisibility(View.VISIBLE);
        }
    }

    private void updateButtonsVisibility(boolean hasOpenShift) {
        btnStartShift.setVisibility(hasOpenShift ? View.GONE : View.VISIBLE);
        btnEndShift.setVisibility(hasOpenShift ? View.VISIBLE : View.GONE);
        btnRecordTransaction.setVisibility(hasOpenShift ? View.VISIBLE : View.GONE);
    }

    private String formatCurrency(Double amount) {
        if (amount == null) {
            return "0₫";
        }
        return currencyFormat.format(amount);
    }
}

