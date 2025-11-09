package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Locale;
import java.util.TimeZone;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "extra_order_id";

    private OrderDetailViewModel viewModel;
    private OrderDetailItemAdapter adapter;

    private TextView tvTableNames, tvStaffName, tvOrderDate, tvStatus, tvTotalAmount;
    private ImageButton btnEditTables, btnEditItems;

    // SỬA: Thêm ActivityResultLauncher để xử lý kết quả trả về từ TableListActivity
    private ActivityResultLauncher<Intent> editTableLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_order_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // View Binding
        tvTableNames = findViewById(R.id.tv_detail_tables);
        tvStaffName = findViewById(R.id.tv_detail_staff);
        tvOrderDate = findViewById(R.id.tv_detail_date);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvTotalAmount = findViewById(R.id.tv_detail_total);
        btnEditTables = findViewById(R.id.btn_edit_tables);
        btnEditItems = findViewById(R.id.btn_edit_items);

        // RecyclerView Setup
        RecyclerView recyclerView = findViewById(R.id.rv_order_detail_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailItemAdapter();
        recyclerView.setAdapter(adapter);

        // SỬA: Khởi tạo launcher
        setupLaunchers();

        // ViewModel Setup
        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);

        // Observe ViewModel
        observeViewModel();

        // Get Order ID from Intent and fetch data
        String orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        viewModel.fetchOrderDetails(orderId);
    }

    // SỬA: Hàm mới để khởi tạo các ActivityResultLauncher
    private void setupLaunchers() {
        editTableLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        ArrayList<String> newTableIds = result.getData().getStringArrayListExtra("updatedTableIds");
                        // TODO: Gọi ViewModel để cập nhật bàn lên server
                        Toast.makeText(this, "Bàn mới đã được chọn: " + (newTableIds != null ? newTableIds.toString() : "null"), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    // SỬA: Hàm mới để gán sự kiện cho các nút edit, gọi sau khi có dữ liệu order
    private void setupEditListeners(Order order) {
        btnEditTables.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, TableListActivity.class);
            // Gửi danh sách ID của các bàn hiện tại đến TableListActivity
            intent.putStringArrayListExtra(TableListActivity.EXTRA_INITIAL_TABLE_IDS, new ArrayList<>(order.getTableIds()));
            editTableLauncher.launch(intent);
        });

        btnEditItems.setOnClickListener(v -> {
            // Logic cho sửa món ăn sẽ làm sau
            Toast.makeText(this, "Chức năng sửa món ăn sẽ được phát triển sau.", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        viewModel.order.observe(this, order -> {
            if (order != null) {
                updateUi(order);
                // SỬA: Gán listener sau khi có dữ liệu order để đảm bảo không bị null
                setupEditListeners(order);
            }
        });

        viewModel.isLoading.observe(this, isLoading -> {
            // Có thể thêm ProgressBar nếu muốn
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
