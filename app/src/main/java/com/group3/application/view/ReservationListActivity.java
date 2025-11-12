package com.group3.application.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
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

import java.util.Calendar;

public class ReservationListActivity extends AppCompatActivity {

    private ReservationViewModel viewModel;
    private ReservationListAdapter reservationListAdapter;
    private String tableId;

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
            }
        });

        viewModel.fetchReservationsByTable(tableId);

        FloatingActionButton fabAddReservation = findViewById(R.id.fab_add_reservation);
        fabAddReservation.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_add_reservation, null);
            builder.setView(dialogView);

            final TextInputEditText etCustomerName = dialogView.findViewById(R.id.et_customer_name);
            final TextInputEditText etCustomerPhone = dialogView.findViewById(R.id.et_customer_phone);
            final TextInputEditText etReservationDate = dialogView.findViewById(R.id.et_reservation_date);
            final TextInputEditText etReservationTime = dialogView.findViewById(R.id.et_reservation_time);
            final TextInputEditText etNumGuests = dialogView.findViewById(R.id.et_number_of_guests);

            final Calendar calendar = Calendar.getInstance();

            etReservationDate.setOnClickListener(v -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        (datePicker, year, month, day) -> {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            etReservationDate.setText(String.format("%d-%02d-%02d", year, month + 1, day));
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            });

            etReservationTime.setOnClickListener(v -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        (timePicker, hour, minute) -> {
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            etReservationTime.setText(String.format("%02d:%02d", hour, minute));
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true);
                timePickerDialog.show();
            });

            builder.setTitle("Add New Reservation")
                    .setPositiveButton("Save", (dialog, which) -> {
                        String customerName = etCustomerName.getText().toString();
                        String customerPhone = etCustomerPhone.getText().toString();
                        String reservationDate = etReservationDate.getText().toString();
                        String reservationTime = etReservationTime.getText().toString();
                        String reservationDateTime = reservationDate + " " + reservationTime;
                        int numGuests = Integer.parseInt(etNumGuests.getText().toString());

                        viewModel.createReservation(customerName, customerPhone, reservationDateTime, numGuests, tableId);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}
