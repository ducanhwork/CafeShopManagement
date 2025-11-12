package com.group3.application.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.model.entity.Shift;
import com.group3.application.viewmodel.ShiftViewModel;

import java.time.format.DateTimeFormatter;

public class ShiftActivity extends AppCompatActivity {

    private ShiftViewModel shiftViewModel;
    
    // UI Components
    private LinearLayout layoutNoShift;
    private LinearLayout layoutActiveShift;
    private Button btnStartShift;
    private Button btnEndShift;
    private Button btnRecordTransaction;
    private Button btnViewTransactions;
    private TextView tvShiftId;
    private TextView tvStartTime;
    private TextView tvOpeningCash;
    private TextView tvCurrentBalance;
    private TextView tvTotalCashIn;
    private TextView tvTotalCashOut;
    private TextView tvTransactionCount;
    private ProgressBar progressBar;
    
    private Shift currentShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);

        setupToolbar();
        initViews();
        
        shiftViewModel = new ViewModelProvider(this).get(ShiftViewModel.class);
        
        observeViewModel();
        setupClickListeners();
        
        // Load current shift
        shiftViewModel.fetchCurrentShift();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Shift Management");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        layoutNoShift = findViewById(R.id.layoutNoShift);
        layoutActiveShift = findViewById(R.id.layoutActiveShift);
        btnStartShift = findViewById(R.id.btnStartShift);
        btnEndShift = findViewById(R.id.btnEndShift);
        btnRecordTransaction = findViewById(R.id.btnRecordTransaction);
        btnViewTransactions = findViewById(R.id.btnViewTransactions);
        tvShiftId = findViewById(R.id.tvShiftId);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvOpeningCash = findViewById(R.id.tvOpeningCash);
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        tvTotalCashIn = findViewById(R.id.tvTotalCashIn);
        tvTotalCashOut = findViewById(R.id.tvTotalCashOut);
        tvTransactionCount = findViewById(R.id.tvTransactionCount);
        progressBar = findViewById(R.id.progressBar);
    }

    private void observeViewModel() {
        shiftViewModel.getCurrentShift().observe(this, shift -> {
            if (shift != null) {
                currentShift = shift;
                showActiveShift(shift);
                // Fetch cash balance
                shiftViewModel.fetchCashBalance();
            } else {
                showNoShift();
            }
        });

        shiftViewModel.getCashBalance().observe(this, balance -> {
            if (balance != null) {
                tvCurrentBalance.setText(String.format("₫%.2f", balance.getCurrentBalance()));
                tvTotalCashIn.setText(String.format("₫%.2f", balance.getTotalCashIn()));
                tvTotalCashOut.setText(String.format("₫%.2f", balance.getTotalCashOut()));
                tvTransactionCount.setText(String.valueOf(balance.getTransactionCount()));
            }
        });

        shiftViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnStartShift.setEnabled(!isLoading);
            btnEndShift.setEnabled(!isLoading);
        });

        shiftViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        shiftViewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnStartShift.setOnClickListener(v -> showStartShiftDialog());
        btnEndShift.setOnClickListener(v -> showEndShiftDialog());
        btnRecordTransaction.setOnClickListener(v -> showRecordTransactionDialog());
        btnViewTransactions.setOnClickListener(v -> {
            if (currentShift != null) {
                // You can create a TransactionListActivity to show all transactions
                Toast.makeText(this, "View transactions feature - to be implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNoShift() {
        layoutNoShift.setVisibility(View.VISIBLE);
        layoutActiveShift.setVisibility(View.GONE);
    }

    private void showActiveShift(Shift shift) {
        layoutNoShift.setVisibility(View.GONE);
        layoutActiveShift.setVisibility(View.VISIBLE);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        tvShiftId.setText("Shift ID: " + shift.getId().toString().substring(0, 8));
        tvStartTime.setText("Started: " + shift.getStartTime().format(formatter));
        tvOpeningCash.setText(String.format("Opening: ₫%.2f", shift.getOpeningCash()));
    }

    private void showStartShiftDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start Shift");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_start_shift, null);
        TextInputLayout tilOpeningCash = dialogView.findViewById(R.id.tilOpeningCash);
        EditText etOpeningCash = dialogView.findViewById(R.id.etOpeningCash);
        
        builder.setView(dialogView);
        builder.setPositiveButton("Start", (dialog, which) -> {
            String openingCashStr = etOpeningCash.getText().toString().trim();
            
            if (openingCashStr.isEmpty()) {
                Toast.makeText(this, "Please enter opening cash amount", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double openingCash = Double.parseDouble(openingCashStr);
                if (openingCash < 0) {
                    Toast.makeText(this, "Opening cash must be >= 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                shiftViewModel.startShift(openingCash);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEndShiftDialog() {
        if (currentShift == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("End Shift");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_end_shift, null);
        TextInputLayout tilClosingCash = dialogView.findViewById(R.id.tilClosingCash);
        EditText etClosingCash = dialogView.findViewById(R.id.etClosingCash);
        
        builder.setView(dialogView);
        builder.setPositiveButton("End Shift", (dialog, which) -> {
            String closingCashStr = etClosingCash.getText().toString().trim();
            
            if (closingCashStr.isEmpty()) {
                Toast.makeText(this, "Please enter closing cash amount", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double closingCash = Double.parseDouble(closingCashStr);
                if (closingCash < 0) {
                    Toast.makeText(this, "Closing cash must be >= 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                shiftViewModel.endShift(closingCash);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showRecordTransactionDialog() {
        if (currentShift == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Record Cash Transaction");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_record_transaction, null);
        TextInputLayout tilAmount = dialogView.findViewById(R.id.tilAmount);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        RadioGroup rgTransactionType = dialogView.findViewById(R.id.rgTransactionType);
        TextInputLayout tilDescription = dialogView.findViewById(R.id.tilDescription);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        TextInputLayout tilReference = dialogView.findViewById(R.id.tilReference);
        EditText etReference = dialogView.findViewById(R.id.etReference);
        
        builder.setView(dialogView);
        builder.setPositiveButton("Record", (dialog, which) -> {
            String amountStr = etAmount.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String reference = etReference.getText().toString().trim();
            
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(this, "Amount must be > 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String transactionType;
                int selectedId = rgTransactionType.getCheckedRadioButtonId();
                if (selectedId == R.id.rbCashIn) {
                    transactionType = "CASH_IN";
                } else if (selectedId == R.id.rbCashOut) {
                    transactionType = "CASH_OUT";
                } else if (selectedId == R.id.rbRefund) {
                    transactionType = "REFUND";
                } else {
                    Toast.makeText(this, "Please select transaction type", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                shiftViewModel.recordCashTransaction(amount, transactionType, description, reference);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
