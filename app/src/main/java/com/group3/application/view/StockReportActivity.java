package com.group3.application.view; // (Sửa package)

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.group3.application.R;
import com.group3.application.model.dto.StockReportDTO;
import com.group3.application.view.adapter.StockReportAdapter;
import com.group3.application.viewmodel.StockReportViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class StockReportActivity extends AppCompatActivity {

    private StockReportViewModel viewModel;
    private StockReportAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvDate, tvTotalItems, tvLowStock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);
        viewModel = new ViewModelProvider(this).get(StockReportViewModel.class);
        setupViews();
        setupAdapter();
        observeViewModel();
        viewModel.fetchReport();
    }

    private void setupViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_report);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progress_bar_report);
        tvDate = findViewById(R.id.tv_report_date);
        tvTotalItems = findViewById(R.id.tv_report_total_items_value);
        tvLowStock = findViewById(R.id.tv_report_low_stock_value);

        tvDate.setText("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void setupAdapter() {
        RecyclerView rv = findViewById(R.id.rv_stock_details);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StockReportAdapter();
        rv.setAdapter(adapter);
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

        viewModel.report.observe(this, report -> {
            if (report != null) {
                updateSummaryUI(report);
                adapter.setData(report.getDetails());
            }
        });
    }

    private void updateSummaryUI(StockReportDTO report) {
        tvTotalItems.setText(String.valueOf(report.getTotalItems()));
        tvLowStock.setText(String.valueOf(report.getLowStockItems()));
    }
}