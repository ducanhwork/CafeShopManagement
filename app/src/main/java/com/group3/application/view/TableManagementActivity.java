package com.group3.application.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.adapter.TableManagementAdapter;
import com.group3.application.viewmodel.TableManagementViewModel;

import java.util.ArrayList;
import java.util.List;

public class TableManagementActivity extends AppCompatActivity {

    private TableManagementViewModel viewModel;
    private RecyclerView recyclerView;
    private TableManagementAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_management);

        setupToolbar();
        initViews();
        
        viewModel = new ViewModelProvider(this).get(TableManagementViewModel.class);
        
        setupRecyclerView();
        observeViewModel();
        
        fabAdd.setOnClickListener(v -> showAddTableDialog());
        swipeRefresh.setOnRefreshListener(() -> loadTables());
        
        loadTables();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Table Management");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupRecyclerView() {
        adapter = new TableManagementAdapter(new ArrayList<>(), this::onTableClick, this::onTableEdit, this::onTableDelete);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getTables().observe(this, tables -> {
            if (tables != null) {
                adapter.updateData(tables);
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

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTables() {
        viewModel.fetchAllTables(null, null, null, 0, 100, "name,asc");
    }

    private void onTableClick(TableInfo table) {
        // Show table details
        showTableDetailsDialog(table);
    }

    private void onTableEdit(TableInfo table) {
        showEditTableDialog(table);
    }

    private void onTableDelete(TableInfo table) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Table")
                .setMessage("Are you sure you want to delete " + table.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteTable(table.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddTableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Table");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_table_form, null);
        setupTableFormDialog(dialogView, null);
        
        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            saveTable(dialogView, null);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditTableDialog(TableInfo table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Table");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_table_form, null);
        setupTableFormDialog(dialogView, table);
        
        builder.setView(dialogView);
        builder.setPositiveButton("Update", (dialog, which) -> {
            saveTable(dialogView, table);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setupTableFormDialog(View dialogView, TableInfo table) {
        EditText etName = dialogView.findViewById(R.id.etTableName);
        EditText etSeatCount = dialogView.findViewById(R.id.etSeatCount);
        AutoCompleteTextView acLocation = dialogView.findViewById(R.id.acLocation);
        AutoCompleteTextView acStatus = dialogView.findViewById(R.id.acStatus);
        
        // Setup location dropdown
        String[] locations = {"Indoor", "Outdoor", "Balcony"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_dropdown_item_1line, locations);
        acLocation.setAdapter(locationAdapter);
        
        // Setup status dropdown
        String[] statuses = {"Available", "Occupied", "Reserved"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_dropdown_item_1line, statuses);
        acStatus.setAdapter(statusAdapter);
        
        if (table != null) {
            etName.setText(table.getName());
            etSeatCount.setText(String.valueOf(table.getSeatCount()));
            acLocation.setText(table.getLocation(), false);
            acStatus.setText(table.getStatus(), false);
        } else {
            acStatus.setText("Available", false);
        }
    }

    private void saveTable(View dialogView, TableInfo existingTable) {
        EditText etName = dialogView.findViewById(R.id.etTableName);
        EditText etSeatCount = dialogView.findViewById(R.id.etSeatCount);
        AutoCompleteTextView acLocation = dialogView.findViewById(R.id.acLocation);
        AutoCompleteTextView acStatus = dialogView.findViewById(R.id.acStatus);
        
        String name = etName.getText().toString().trim();
        String seatCountStr = etSeatCount.getText().toString().trim();
        String location = acLocation.getText().toString();
        String status = acStatus.getText().toString();
        
        if (name.isEmpty() || seatCountStr.isEmpty() || location.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            int seatCount = Integer.parseInt(seatCountStr);
            if (seatCount < 1 || seatCount > 20) {
                Toast.makeText(this, "Seat count must be between 1 and 20", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (existingTable != null) {
                viewModel.updateTable(existingTable.getId(), name, location, seatCount, status);
            } else {
                viewModel.createTable(name, location, seatCount, status);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid seat count", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTableDetailsDialog(TableInfo table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(table.getName());
        
        String details = "Location: " + table.getLocation() + "\n" +
                        "Seats: " + table.getSeatCount() + "\n" +
                        "Status: " + table.getStatus() + "\n" +
                        "Available: " + (table.isAvailable() ? "Yes" : "No");
        
        builder.setMessage(details);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
