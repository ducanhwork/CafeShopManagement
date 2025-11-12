package com.group3.application.view; // (Sửa package nếu cần)

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView; // Import TextView

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
import com.group3.application.view.adapter.ItemReportParentAdapter; // Adapter Cha
import com.group3.application.viewmodel.ItemReportViewModel; // VM Mới

import java.math.BigDecimal; // Import BigDecimal
import java.text.DecimalFormat; // Import DecimalFormat
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ItemReportActivity extends AppCompatActivity {
    private ItemReportViewModel viewModel;
    private ItemReportParentAdapter parentAdapter;
    private ProgressBar progressBar;
    private TextInputEditText edtFrom, edtTo;
    private AutoCompleteTextView actFilterBy;
    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat currencyFormatter = new DecimalFormat("#,###,### đ"); // Thêm format tiền

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_report);
        viewModel = new ViewModelProvider(this).get(ItemReportViewModel.class);
        setupViews();
        setupAdapter();
        setupListeners();
        observeViewModel();
        viewModel.fetchReport();
    }

    private void setupViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_report);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progress_bar_report);
        edtFrom = findViewById(R.id.edt_report_from);
        edtTo = findViewById(R.id.edt_report_to);
        actFilterBy = findViewById(R.id.act_report_filter);

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new String[]{"DAY", "WEEK", "MONTH"});
        actFilterBy.setAdapter(filterAdapter);
    }

    private void setupAdapter() {
        RecyclerView rvParent = findViewById(R.id.rv_item_report_parent);
        rvParent.setLayoutManager(new LinearLayoutManager(this));
        parentAdapter = new ItemReportParentAdapter(this);
        rvParent.setAdapter(parentAdapter);
    }

    private void setupListeners() {
        edtFrom.setOnClickListener(v -> showDatePicker(true));
        edtTo.setOnClickListener(v -> showDatePicker(false));

        actFilterBy.setOnItemClickListener((parent, v, position, id) -> {
            String selectedFilter = (String) parent.getItemAtPosition(position);
            viewModel.setFilterBy(selectedFilter);
            viewModel.fetchReport();
        });
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.error.observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        viewModel.dateFrom.observe(this, date -> {
            edtFrom.setText(date.format(displayFormatter));
        });
        viewModel.dateTo.observe(this, date -> {
            edtTo.setText(date.format(displayFormatter));
        });

        viewModel.report.observe(this, reportList -> {
            if (reportList != null) {
                parentAdapter.setData(reportList);
            }
        });
        viewModel.filterBy.observe(this, filter -> {
            parentAdapter.setFilterType(filter);
        });
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

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        if (isDateFrom) {
            if (toDate != null) {
                long toDateMillis = toDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                constraintsBuilder.setEnd(toDateMillis);
                constraintsBuilder.setValidator(DateValidatorPointBackward.before(toDateMillis));
            }
        } else {
            if (fromDate != null) {
                long fromDateMillis = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                constraintsBuilder.setStart(fromDateMillis);
                constraintsBuilder.setValidator(DateValidatorPointForward.from(fromDateMillis));
            }
        }
        builder.setCalendarConstraints(constraintsBuilder.build());

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