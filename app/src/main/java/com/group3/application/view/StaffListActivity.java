
package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.group3.application.R;
import com.group3.application.view.adapter.StaffListAdapter;
import com.group3.application.viewmodel.StaffListViewModel;

public class StaffListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private StaffListViewModel viewModel;
    private StaffListAdapter staffListAdapter;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        RecyclerView recyclerView = findViewById(R.id.rvStaffList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        staffListAdapter = new StaffListAdapter();
        recyclerView.setAdapter(staffListAdapter);

        viewModel = new ViewModelProvider(this).get(StaffListViewModel.class);

        setupObservers();

        viewModel.fetchStaff();

//        FloatingActionButton fabAddReservation = findViewById(R.id.fab_add_staff);
//        fabAddReservation.setOnClickListener(view -> showAddStaffDialog());
    }

    private void setupObservers() {
        viewModel.getStaffs().observe(this, staffs -> {
            if (staffs != null) {
                staffListAdapter.setStaffs(staffs);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                Log.e("ReservationListActivity", error);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            // Show a progress bar or some other loading indicator
        });

//        viewModel.getCreatedReservation().observe(this, reservation -> {
//            if (reservation != null) {
//                Toast.makeText(this, "Reservation created successfully", Toast.LENGTH_SHORT).show();
//                viewModel.fetchReservationsByTable(tableId);
//            }
//        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_dashboard) {
            // Handle dashboard click
        } else if (id == R.id.nav_reservations) {
            Intent intent = new Intent(this, ReservationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_staff_list) {
            Intent intent = new Intent(this, StaffListActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

//    private void showAddStaffDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_add_reservation, null);
//        builder.setView(dialogView);
//
//        final TextInputEditText etCustomerName = dialogView.findViewById(R.id.et_customer_name);
//        final TextInputEditText etCustomerPhone = dialogView.findViewById(R.id.et_customer_phone);
//        final TextInputEditText etReservationTime = dialogView.findViewById(R.id.et_reservation_time);
//        final TextInputEditText etNumGuests = dialogView.findViewById(R.id.et_number_of_guests);
//
//        builder.setTitle("Add New Reservation")
//                .setPositiveButton("Save", (dialog, which) -> {
//                    String customerName = etCustomerName.getText().toString();
//                    String customerPhone = etCustomerPhone.getText().toString();
//                    String reservationTime = etReservationTime.getText().toString();
//                    int numGuests = Integer.parseInt(etNumGuests.getText().toString());
//
//                    viewModel.createReservation(customerName, customerPhone, reservationTime, numGuests, tableId, userId);
//                })
//                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
}
