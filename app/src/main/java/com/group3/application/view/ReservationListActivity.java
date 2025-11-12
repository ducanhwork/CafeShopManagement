package com.group3.application.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.group3.application.view.adapter.ReservationListAdapter;
import com.group3.application.viewmodel.ReservationViewModel;

public class ReservationListActivity extends AppCompatActivity {

    private ReservationViewModel viewModel;
    private ReservationListAdapter reservationListAdapter;
    private String tableId;
    // TODO: Replace with actual user ID from session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tableId = getIntent().getStringExtra("TABLE_ID");

        RecyclerView reservationRecyclerView = findViewById(R.id.reservation_recycler_view);
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reservationListAdapter = new ReservationListAdapter();
        reservationRecyclerView.setAdapter(reservationListAdapter);

        viewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        setupObservers();

        viewModel.fetchReservationsByTable(tableId);

        FloatingActionButton fabAddReservation = findViewById(R.id.fab_add_reservation);
        fabAddReservation.setOnClickListener(view -> showAddReservationDialog());
    }

    private void setupObservers() {
        viewModel.getReservations().observe(this, reservations -> {
            if (reservations != null) {
                reservationListAdapter.setReservations(reservations);
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

        viewModel.getCreatedReservation().observe(this, reservation -> {
            if (reservation != null) {
                Toast.makeText(this, "Reservation created successfully", Toast.LENGTH_SHORT).show();
                viewModel.fetchReservationsByTable(tableId);
            }
        });
    }

    private void showAddReservationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_reservation, null);
        builder.setView(dialogView);

        final TextInputEditText etCustomerName = dialogView.findViewById(R.id.et_customer_name);
        final TextInputEditText etCustomerPhone = dialogView.findViewById(R.id.et_customer_phone);
        final TextInputEditText etReservationTime = dialogView.findViewById(R.id.et_reservation_time);
        final TextInputEditText etNumGuests = dialogView.findViewById(R.id.et_number_of_guests);

        builder.setTitle("Add New Reservation")
                .setPositiveButton("Save", (dialog, which) -> {
                    String customerName = etCustomerName.getText().toString();
                    String customerPhone = etCustomerPhone.getText().toString();
                    String reservationTime = etReservationTime.getText().toString();
                    int numGuests = Integer.parseInt(etNumGuests.getText().toString());

                    viewModel.createReservation(customerName, customerPhone, reservationTime, numGuests, tableId);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
