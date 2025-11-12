package com.group3.application.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.model.dto.RevenueReportDTO;
import com.group3.application.view.adapter.RevenueReportAdapter;
import com.group3.application.viewmodel.ReportViewModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class RevenueReportActivity extends AppCompatActivity {

    private ReportViewModel viewModel;
    private RevenueReportAdapter adapter;

    private ProgressBar progressBar;
    private TextInputEditText edtFrom, edtTo;
    private AutoCompleteTextView actFilterBy;
    private TextView tvTotalRevenue, tvTotalBills, tvAvgRevenue;
    private RecyclerView rvDetails;

    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat currencyFormatter = new DecimalFormat("#,###,### đ");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_report);

        setupViews();
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        setupAdapter();
        setupListeners();

        observeViewModel();
    }

    private void setupViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_report);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progress_bar_report);
        edtFrom = findViewById(R.id.edt_report_from);
        edtTo = findViewById(R.id.edt_report_to);
        actFilterBy = findViewById(R.id.act_report_filter);
        tvTotalRevenue = findViewById(R.id.tv_report_total_revenue);
        tvTotalBills = findViewById(R.id.tv_report_total_bills);
        tvAvgRevenue = findViewById(R.id.tv_report_avg_revenue);
        rvDetails = findViewById(R.id.rv_report_details);

        String[] filterOptions = {"Day", "Week", "Month"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, filterOptions);
        actFilterBy.setAdapter(filterAdapter);
    }

    private void setupAdapter() {
        rvDetails.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RevenueReportAdapter();
        rvDetails.setAdapter(adapter);
    }

    private void setupListeners() {
        edtFrom.setOnClickListener(v -> showDatePicker(true));
        edtTo.setOnClickListener(v -> showDatePicker(false));

        actFilterBy.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFilter = (String) parent.getItemAtPosition(position);
            viewModel.setFilterBy(selectedFilter);
        });
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.error.observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.dateFrom.observe(this, date -> {
            edtFrom.setText(date.format(displayFormatter));
        });

        viewModel.dateTo.observe(this, date -> {
            edtTo.setText(date.format(displayFormatter));
        });

        viewModel.filterBy.observe(this, filter -> {
            actFilterBy.setText(filter, false);
            adapter.setFilterType(filter);
        });

        viewModel.report.observe(this, report -> {
            if (report != null) {
                updateSummaryUI(report);
                adapter.setData(report.getDetails());
            } else {
                updateSummaryUI(new RevenueReportDTO());
                adapter.setData(null);
            }
        });
    }

    private void updateSummaryUI(RevenueReportDTO report) {
        tvTotalRevenue.setText("Total Revenue: " + formatCurrency(report.getTotalRevenue()));
        tvTotalBills.setText("Number of Bills: " + report.getTotalBills());
        tvAvgRevenue.setText("Avg Revenue / Bill: " + formatCurrency(report.getAvgRevenuePerBill()));
    }

    private void showDatePicker(boolean isDateFrom) {
        LocalDate fromDate = viewModel.dateFrom.getValue();
        LocalDate toDate = viewModel.dateTo.getValue();

        long currentSelection = MaterialDatePicker.todayInUtcMilliseconds();
        if (isDateFrom && fromDate != null) {
            currentSelection = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else if (!isDateFrom && toDate != null) {
            currentSelection = toDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isDateFrom ? "Chọn ngày bắt đầu" : "Chọn ngày kết thúc")
                .setSelection(currentSelection);

        // --- Bắt đầu logic Validation ---
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        if (isDateFrom) {
            // Nếu chọn NGÀY BẮT ĐẦU, không được chọn sau ngày kết thúc
            if (toDate != null) {
                long toDateMillis = toDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                constraintsBuilder.setEnd(toDateMillis);
                constraintsBuilder.setValidator(DateValidatorPointBackward.before(toDateMillis));
            }
        } else {
            // Nếu chọn NGÀY KẾT THÚC, không được chọn trước ngày bắt đầu
            if (fromDate != null) {
                long fromDateMillis = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                constraintsBuilder.setStart(fromDateMillis);
                constraintsBuilder.setValidator(DateValidatorPointForward.from(fromDateMillis));
            }
        }
        builder.setCalendarConstraints(constraintsBuilder.build());
        // --- Kết thúc logic Validation ---

        MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            LocalDate selectedDate = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (isDateFrom) {
                viewModel.setDateFrom(selectedDate);
            } else {
                viewModel.setDateTo(selectedDate);
            }

            viewModel.fetchReport();
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return currencyFormatter.format(amount) + " đ";
    }
}