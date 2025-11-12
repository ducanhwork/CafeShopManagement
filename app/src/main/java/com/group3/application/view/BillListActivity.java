package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.group3.application.R;
import com.group3.application.model.dto.BillResponse;
import com.group3.application.model.dto.BillSummaryDTO;
import com.group3.application.model.entity.Order;
import com.group3.application.view.adapter.BillSummaryAdapter;
import com.group3.application.viewmodel.BillListViewModel;

import java.time.LocalDate;

public class BillListActivity extends AppCompatActivity implements BillSummaryAdapter.OnBillClickListener {

    private BillListViewModel viewModel;
    private BillSummaryAdapter adapter;

    private ActivityResultLauncher<Intent> detailLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        setupToolbar();

        detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
            }
        );

        RecyclerView rvBillList = findViewById(R.id.rv_bill_list);
        rvBillList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BillSummaryAdapter(this);
        rvBillList.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(BillListViewModel.class);
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.fetchBills(null);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_bill_list);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        viewModel.getBillList().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        adapter.setBills(resource.data);
                    } else {
                        Toast.makeText(this, "Không có hóa đơn nào", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ERROR:
                    Toast.makeText(this, "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    Log.e("BillListActivity", "Lỗi: " + resource.message);
                    break;
            }
        });
    }

    @Override
    public void onBillClick(BillSummaryDTO bill) {
        if (bill == null || bill.getBillId() == null) {
            Toast.makeText(this, "Lỗi: Không thể xem chi tiết hóa đơn này.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, BillDetailActivity.class);
        intent.putExtra("origin", "list");
        intent.putExtra(BillDetailActivity.EXTRA_BILL_ID, bill.getBillId().toString());
        detailLauncher.launch(intent);
    }
}
