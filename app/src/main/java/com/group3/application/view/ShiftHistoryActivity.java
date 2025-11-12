package com.group3.application.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.model.entity.Shift;
import com.group3.application.view.adapter.ShiftHistoryAdapter;
import com.group3.application.viewmodel.ShiftViewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ShiftHistoryActivity extends AppCompatActivity {

    private ShiftViewModel viewModel;
    private RecyclerView recyclerView;
    private ShiftHistoryAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private Button btnFilter;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Date filterStartDate;
    private Date filterEndDate;
    private String filterStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_history);

        setupToolbar();
        initViews();

        viewModel = new ViewModelProvider(this).get(ShiftViewModel.class);

        setupRecyclerView();
        observeViewModel();

        btnFilter.setOnClickListener(v -> showFilterDialog());
        swipeRefresh.setOnRefreshListener(() -> loadShifts());

        loadShifts();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Shift History");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        btnFilter = findViewById(R.id.btnFilter);
    }

    private void setupRecyclerView() {
        adapter = new ShiftHistoryAdapter(new ArrayList<>(), this::onShiftClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getShiftHistory().observe(this, shifts -> {
            if (shifts != null) {
                adapter.updateData(shifts);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(isLoading);
            } else {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadShifts() {
        viewModel.fetchAllShifts();
    }

    private void onShiftClick(Shift shift) {
        showShiftDetailsDialog(shift);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Shifts");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_shift_filter, null);

        Button btnStartDate = dialogView.findViewById(R.id.btnStartDate);
        Button btnEndDate = dialogView.findViewById(R.id.btnEndDate);
        AutoCompleteTextView acStatus = dialogView.findViewById(R.id.acStatus);

        // Setup status dropdown
        String[] statuses = {"All", "Active", "Completed"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, statuses);
        acStatus.setAdapter(statusAdapter);
        acStatus.setText(filterStatus != null ? filterStatus : "All", false);

        // Set date text
        if (filterStartDate != null) {
            btnStartDate.setText(dateFormat.format(filterStartDate));
        }
        if (filterEndDate != null) {
            btnEndDate.setText(dateFormat.format(filterEndDate));
        }

        // Date pickers
        btnStartDate.setOnClickListener(v -> showDatePicker(date -> {
            filterStartDate = date;
            btnStartDate.setText(dateFormat.format(date));
        }));

        btnEndDate.setOnClickListener(v -> showDatePicker(date -> {
            filterEndDate = date;
            btnEndDate.setText(dateFormat.format(date));
        }));

        builder.setView(dialogView);
        builder.setPositiveButton("Apply", (dialog, which) -> {
            filterStatus = acStatus.getText().toString();
            if ("All".equals(filterStatus)) {
                filterStatus = null;
            }
            applyFilters();
        });
        builder.setNegativeButton("Clear", (dialog, which) -> {
            filterStartDate = null;
            filterEndDate = null;
            filterStatus = null;
            loadShifts();
        });
        builder.setNeutralButton("Cancel", null);
        builder.show();
    }

    private void showDatePicker(DatePickerListener listener) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    listener.onDateSelected(selectedDate.getTime());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void applyFilters() {
        // In a real app, apply filters to the API call
        // For now, just reload
        Toast.makeText(this, "Applying filters...", Toast.LENGTH_SHORT).show();
        loadShifts();
    }

    private void showShiftDetailsDialog(Shift shift) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Shift Details");

        StringBuilder details = new StringBuilder();
        details.append("Employee: ").append(shift.getUserFullName()).append("\n\n");
        details.append("Start: ").append(formatDateTime(shift.getStartTime())).append("\n");
        
        if (shift.getEndTime() != null) {
            details.append("End: ").append(formatDateTime(shift.getEndTime())).append("\n");
        }
        
        details.append("Duration: ").append(shift.getDurationMinutes()).append(" minutes\n\n");
        details.append("Opening Cash: ₫").append(shift.getOpeningCash()).append("\n");
        
        if (shift.getClosingCash() != null) {
            details.append("Closing Cash: ₫").append(shift.getClosingCash()).append("\n");
        }
        
        if (shift.getCashDiscrepancy() != null && shift.getCashDiscrepancy().doubleValue() != 0) {
            details.append("Cash Discrepancy: ₫").append(shift.getCashDiscrepancy()).append("\n");
        }
        
        details.append("\nStatus: ").append(shift.getStatus());

        builder.setMessage(details.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    interface DatePickerListener {
        void onDateSelected(Date date);
    }
}
