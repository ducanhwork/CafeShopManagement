package com.group3.application.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.model.entity.Role;
import com.group3.application.model.entity.User;
import com.group3.application.view.adapter.StaffListAdapter;
import com.group3.application.viewmodel.StaffListViewModel;

import java.util.List;
import java.util.UUID;

public class StaffListActivity extends AppCompatActivity {

    private StaffListViewModel viewModel;
    private StaffListAdapter staffListAdapter;
    private List<Role> roles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        RecyclerView recyclerView = findViewById(R.id.rvStaffList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupToolbar();
        setTitle("Staff List");

        staffListAdapter = new StaffListAdapter();
        recyclerView.setAdapter(staffListAdapter);

        viewModel = new ViewModelProvider(this).get(StaffListViewModel.class);

        setupObservers();

        viewModel.fetchStaff();
        viewModel.fetchRoles();

        FloatingActionButton fabAddReservation = findViewById(R.id.fab_add_staff);
        fabAddReservation.setOnClickListener(view -> showAddStaffDialog());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reservations");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupObservers() {
        viewModel.getStaffs().observe(this, staffs -> {
            if (staffs != null) {
                staffListAdapter.setStaffs(staffs);
            }
        });

        viewModel.getRoles().observe(this, roles -> {
            if (roles != null) {
                this.roles = roles;
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                Log.e("StaffListActivity", error);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            // Show a progress bar or some other loading indicator
        });

        viewModel.getCreatedStaff().observe(this, staff -> {
            if (staff != null) {
                Toast.makeText(this, "Staff created successfully", Toast.LENGTH_SHORT).show();
                viewModel.fetchStaff();
            }
        });
    }

    private void showAddStaffDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_staff, null);
        builder.setView(dialogView);

        final TextInputEditText etFullName = dialogView.findViewById(R.id.etFullName);
        final TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        final TextInputEditText etMobile = dialogView.findViewById(R.id.etMobile);
        final AutoCompleteTextView actvRole = dialogView.findViewById(R.id.actvRole);
        final TextInputEditText etPassword = dialogView.findViewById(R.id.etPassword);

        if (roles != null) {
            ArrayAdapter<Role> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
            actvRole.setAdapter(adapter);
        }

        builder.setTitle("Add New Staff")
                .setPositiveButton("Add", (dialog, which) -> {
                    String fullName = etFullName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String mobile = etMobile.getText().toString().trim();
                    String roleName = actvRole.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (fullName.isEmpty() || email.isEmpty() || mobile.isEmpty() || roleName.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Role selectedRole = null;
                    if (roles != null) {
                        for (Role r : roles) {
                            if (r.getName().equalsIgnoreCase(roleName)) {
                                selectedRole = r;
                                break;
                            }
                        }
                    }

                    if (selectedRole == null) {
                        Toast.makeText(this, "Please select a valid role from the list.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    viewModel.addStaff(email, password, fullName, mobile, selectedRole.getId());

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
