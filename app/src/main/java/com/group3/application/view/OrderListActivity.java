package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group3.application.R;
import com.group3.application.model.entity.Order;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.entity.User;
import com.group3.application.view.adapter.OrderListAdapter;
import com.group3.application.viewmodel.OrderListViewModel;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderListActivity extends AppCompatActivity implements OrderListAdapter.OnOrderClickListener {

    private OrderListViewModel viewModel;
    private OrderListAdapter adapter;
    private ProgressBar progressBar;

    private AutoCompleteTextView actvStatus, actvStaff, actvTable;
    private List<User> userList = new ArrayList<>();
    private List<TableInfo> tableList = new ArrayList<>();
    private ActivityResultLauncher<Intent> detailLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Toast.makeText(this, "Đang cập nhật danh sách...", Toast.LENGTH_SHORT).show();
                        viewModel.fetchOrders();
                    }
                }
        );

        progressBar = findViewById(R.id.progress_bar);
        actvStatus = findViewById(R.id.actv_status);
        actvStaff = findViewById(R.id.actv_staff);
        actvTable = findViewById(R.id.actv_table);
        FloatingActionButton fabAddOrder = findViewById(R.id.fab_add_order);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderListAdapter(this); 
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(OrderListViewModel.class);

        observeViewModel();
        setupFilterListeners();

        fabAddOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderListActivity.this, TableListActivity.class);
            startActivity(intent);
        });

        viewModel.fetchFilterData(); 
        viewModel.fetchOrders();    
    }

    private void observeViewModel() {
        viewModel.orders.observe(this, orders -> {
            if (orders != null) {
                adapter.setData(orders);
                if(orders.isEmpty()){
                    Toast.makeText(this, "Không tìm thấy đơn hàng nào", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.isLoading.observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.error.observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.users.observe(this, users -> {
            userList = users;
            List<String> userNames = new ArrayList<>();
            userNames.add("Tất cả NV");
            userNames.addAll(users.stream().map(User::getFullname).collect(Collectors.toList()));
            ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, userNames);
            actvStaff.setAdapter(userAdapter);
        });

        viewModel.tables.observe(this, tables -> {
            tableList = tables;
            List<String> tableNames = new ArrayList<>();
            tableNames.add("Tất cả bàn"); 
            tableNames.addAll(tables.stream().map(TableInfo::getName).collect(Collectors.toList()));
            ArrayAdapter<String> tableAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tableNames);
            actvTable.setAdapter(tableAdapter);
        });

        List<String> statusList = Arrays.asList("ALL", "SERVING", "PAID");
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statusList);
        actvStatus.setAdapter(statusAdapter);

    }

    private void setupFilterListeners() {
        actvStatus.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStatus = (String) parent.getItemAtPosition(position);
            viewModel.setStatusFilter(selectedStatus);
        });

        actvStaff.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                viewModel.setStaffIdFilter(null); 
            } else {
                User selectedUser = userList.get(position - 1); 
                viewModel.setStaffIdFilter(selectedUser.getId().toString());
            }
        });

        actvTable.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                viewModel.setTableIdFilter(null); 
            } else {
                TableInfo selectedTable = tableList.get(position - 1); 
                viewModel.setTableIdFilter(selectedTable.getId());
            }
        });
    }

    public void onOrderClick(Order order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getId());
        detailLauncher.launch(intent);
    }
}
