package com.group3.application.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.group3.application.R;
import com.group3.application.model.entity.Reservation;
import com.group3.application.viewmodel.ReservationViewModel;

import java.time.format.DateTimeFormatter;

public class ReservationDetailActivity extends AppCompatActivity {

    private ReservationViewModel reservationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        reservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        Reservation reservation = (Reservation) getIntent().getSerializableExtra("reservation");

        if (reservation != null) {
            TextView customerNameTextView = findViewById(R.id.customer_name_text_view);
            TextView customerPhoneTextView = findViewById(R.id.customer_phone_text_view);
            TextView reservationTimeTextView = findViewById(R.id.reservation_time_text_view);
            TextView numGuestsTextView = findViewById(R.id.num_guests_text_view);
            TextView statusTextView = findViewById(R.id.status_text_view);
            Button cancelButton = findViewById(R.id.cancel_button);

            customerNameTextView.setText(reservation.getCustomerName());
            customerPhoneTextView.setText(reservation.getCustomerPhone());
            reservationTimeTextView.setText(reservation.getReservationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            numGuestsTextView.setText(String.valueOf(reservation.getNumGuests()));
            statusTextView.setText(reservation.getStatus());

            if ("PENDING".equalsIgnoreCase(reservation.getStatus()) || "CONFIRMED".equalsIgnoreCase(reservation.getStatus())) {
                cancelButton.setVisibility(View.VISIBLE);
                cancelButton.setOnClickListener(v -> {
                    reservationViewModel.cancelReservation(reservation);
                });
            }
        }

        reservationViewModel.getReservationCancelled().observe(this, cancelled -> {
            if (cancelled) {
                Toast.makeText(this, "Reservation Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        reservationViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}