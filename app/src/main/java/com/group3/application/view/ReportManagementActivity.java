package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.group3.application.R;

public class ReportManagementActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialCardView cardRevenueReport, cardItemReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_management);

        toolbar = findViewById(R.id.toolbar_report_management);
        cardRevenueReport = findViewById(R.id.card_revenue_report);
        cardItemReport = findViewById(R.id.card_item_report);

        setupToolbar();
        setupClickListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        cardRevenueReport.setOnClickListener(v -> {
            Intent intent = new Intent(ReportManagementActivity.this, RevenueReportActivity.class);
            startActivity(intent);
        });

        cardItemReport.setOnClickListener(v -> {
             Intent intent = new Intent(ReportManagementActivity.this, ItemReportActivity.class);
             startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
