package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.model.dto.OrderDetailItemDTO;
import com.group3.application.model.dto.OrderItemDTO;
import com.group3.application.model.entity.Order;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.adapter.OrderDetailItemAdapter;
import com.group3.application.viewmodel.OrderDetailViewModel;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "extra_order_id";

    private OrderDetailViewModel viewModel;
    private OrderDetailItemAdapter adapter;

    private TextView tvTableNames, tvStaffName, tvOrderDate, tvStatus, tvTotalAmount;
    private Menu menu;

    private ExtendedFloatingActionButton fabSaveChanges;

    private AutoCompleteTextView actStatus;
    private TextInputEditText edtNote;

    private ActivityResultLauncher<Intent> editItemsLauncher;
    private ActivityResultLauncher<Intent> editTablesLauncher;
    private String orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        setupViews();
        setupLaunchers();

        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);
        observeViewModel();

        orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (viewModel.order.getValue() == null) {
            viewModel.fetchOrderDetails(orderId);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_order_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTableNames = findViewById(R.id.tv_detail_tables);
        tvStaffName = findViewById(R.id.tv_detail_staff);
        tvOrderDate = findViewById(R.id.tv_detail_date);
        tvTotalAmount = findViewById(R.id.tv_detail_total);
        actStatus = findViewById(R.id.act_detail_status);
        edtNote = findViewById(R.id.edt_detail_note);
        fabSaveChanges = findViewById(R.id.fab_save_changes);
        String[] orderStatuses = new String[] {"SERVING", "PAID", "CANCELLED", "PENDING"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                orderStatuses
        );

        fabSaveChanges.setOnClickListener(v -> {
            // 1. Lấy dữ liệu mới từ UI
            String newStatus = actStatus.getText().toString();
            String newNote = edtNote.getText().toString();

            // 2. Ra lệnh cho ViewModel gửi lên server
            // (ViewModel của bạn từ lần trước đã có hàm này)
            viewModel.updateOrderOnServer(newStatus, newNote);
        });
        actStatus.setAdapter(statusAdapter);

        RecyclerView recyclerView = findViewById(R.id.rv_order_detail_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailItemAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setupLaunchers() {
        editItemsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        List<OrderItemDTO> items = (List<OrderItemDTO>) result.getData().getSerializableExtra("updatedItems");
                        if (items != null) {
                            viewModel.updateItems(items);
                            fabSaveChanges.show();
                        }
                    }
                }
        );
        actStatus.setOnItemClickListener((parent, view, position, id) -> {
            fabSaveChanges.show(); // Hiện nút khi Status thay đổi
        });
        edtNote.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                fabSaveChanges.show(); // Hiện nút khi Note thay đổi
            }
        });
    }

    private void setupEditListeners(Order order) {
        findViewById(R.id.btn_edit_items).setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, OrderHostActivity.class);
            intent.putExtra(OrderHostActivity.EXTRA_EDIT_ORDER_ID, order.getId());
            intent.putExtra(OrderHostActivity.EXTRA_START_FRAGMENT, "PRODUCTS");

            List<OrderItemDTO> currentItems = order.getItems().stream()
                    .map(item -> new OrderItemDTO(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity()))
                    .collect(Collectors.toList());

            intent.putExtra("initialOrderItems", (Serializable) currentItems);
            editItemsLauncher.launch(intent);
        });
    }

    private void observeViewModel() {
        viewModel.order.observe(this, order -> {
            if (order != null) {
                updateUi(order);
                setupEditListeners(order);
            }
        });

        viewModel.updateResult.observe(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    Toast.makeText(this, "Lưu thay đổi thành công!", Toast.LENGTH_SHORT).show();
                    setResult(AppCompatActivity.RESULT_OK);
                } else {
                    Toast.makeText(this, "Lỗi khi lưu: " + result.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        viewModel.error.observe(this, error -> {
            if (error != null) Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
        });

        viewModel.updateResult.observe(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    Toast.makeText(this, "Lưu thay đổi thành công!", Toast.LENGTH_SHORT).show();
                    // SỬA: Ẩn nút FAB
                    fabSaveChanges.hide();
                } else {
                    Toast.makeText(this, "Lỗi khi lưu: " + result.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateUi(Order order) {
        if (order == null) return;

        tvTableNames.setText("Bàn: " + String.join(", ", order.getTableNames()));
        tvStaffName.setText(order.getStaffName());
        tvOrderDate.setText(formatDate(order.getOrderDate()));
        tvTotalAmount.setText("Tổng: " + formatCurrency(order.getTotalAmount()));
        actStatus.setText(order.getStatus(), false);
        edtNote.setText(order.getNote());

        adapter.setItems(order.getItems());
    }

    private String formatDate(String isoDate) {
        if (isoDate == null) return "";
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(isoDate);
            if (date == null) return isoDate;
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
