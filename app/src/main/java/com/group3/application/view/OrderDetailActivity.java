package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.group3.application.R;
import com.group3.application.model.entity.Order;
import com.group3.application.view.adapter.OrderDetailItemAdapter;
import com.group3.application.viewmodel.OrderDetailViewModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "extra_order_id";

    private OrderDetailViewModel viewModel;
    private OrderDetailItemAdapter adapter;

    private TextView tvTableNames, tvStaffName, tvOrderDate, tvStatus, tvTotalAmount;
    private ImageButton btnEditTables, btnEditItems;
    private Menu menu;
    private List<String> pendingTableIds;

    private ActivityResultLauncher<Intent> editTableLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_order_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTableNames = findViewById(R.id.tv_detail_tables);
        tvStaffName = findViewById(R.id.tv_detail_staff);
        tvOrderDate = findViewById(R.id.tv_detail_date);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvTotalAmount = findViewById(R.id.tv_detail_total);
        btnEditTables = findViewById(R.id.btn_edit_tables);
        btnEditItems = findViewById(R.id.btn_edit_items);

        RecyclerView recyclerView = findViewById(R.id.rv_order_detail_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailItemAdapter();
        recyclerView.setAdapter(adapter);

        setupLaunchers();

        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);
        observeViewModel();

        String orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        viewModel.fetchOrderDetails(orderId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_detail_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save_changes) {
            if (pendingTableIds != null && viewModel.order.getValue() != null) {
                // TODO: Gọi API cập nhật bàn trong ViewModel
                Toast.makeText(this, "Đang lưu thay đổi...", Toast.LENGTH_SHORT).show();
                // viewModel.updateOrderTables(viewModel.order.getValue().getId(), pendingTableIds);
                item.setVisible(false); // Ẩn nút sau khi nhấn
                pendingTableIds = null; // Xóa thay đổi đang chờ
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupLaunchers() {
        editTableLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        ArrayList<String> newTableIds = result.getData().getStringArrayListExtra("updatedTableIds");
                        ArrayList<String> newTableNames = result.getData().getStringArrayListExtra("updatedTableNames");

                        if (newTableIds != null && newTableNames != null && menu != null) {
                            this.pendingTableIds = newTableIds;
                            tvTableNames.setText("Bàn: " + String.join(", ", newTableNames));
                            menu.findItem(R.id.action_save_changes).setVisible(true);
                        }
                    }
                }
        );
    }

    private void setupEditListeners(Order order) {
        btnEditTables.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, TableListActivity.class);
            intent.putStringArrayListExtra(TableListActivity.EXTRA_INITIAL_TABLE_IDS, new ArrayList<>(order.getTableIds()));
            editTableLauncher.launch(intent);
        });

        // SỬA: Thêm logic cho nút sửa món ăn
        btnEditItems.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, OrderHostActivity.class);
            intent.putExtra(OrderHostActivity.EXTRA_EDIT_ORDER_ID, order.getId());
            intent.putExtra(OrderHostActivity.EXTRA_START_FRAGMENT, "PRODUCTS");
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        viewModel.order.observe(this, order -> {
            if (order != null) {
                updateUi(order);
                setupEditListeners(order);
            }
        });

        viewModel.isLoading.observe(this, isLoading -> {
            // Handle loading state
        });

        viewModel.error.observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUi(Order order) {
        if (order == null) return;

        tvTableNames.setText("Bàn: " + String.join(", ", order.getTableNames()));
        tvStaffName.setText(order.getStaffName());
        tvOrderDate.setText(formatDate(order.getOrderDate()));
        tvStatus.setText(order.getStatus());
        tvTotalAmount.setText("Tổng: " + formatCurrency(order.getTotalAmount()));

        adapter.setItems(order.getItems());
    }

    private String formatDate(String isoDate) {
        if (isoDate == null) return "";
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
            return newFormat.format(date);
        } catch (ParseException e) {
            return isoDate;
        }
    }

    private String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###,### đ");
        return formatter.format(amount);
    }
}
