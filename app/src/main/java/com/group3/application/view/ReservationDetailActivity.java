package com.group3.application.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.group3.application.R;
import com.group3.application.model.entity.Reservation;

import java.time.format.DateTimeFormatter;

public class ReservationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Reservation reservation = (Reservation) getIntent().getSerializableExtra("reservation");

        if (reservation != null) {
            TextView customerNameTextView = findViewById(R.id.customer_name_text_view);
            TextView customerPhoneTextView = findViewById(R.id.customer_phone_text_view);
            TextView reservationTimeTextView = findViewById(R.id.reservation_time_text_view);
            TextView numGuestsTextView = findViewById(R.id.num_guests_text_view);
            TextView statusTextView = findViewById(R.id.status_text_view);

            customerNameTextView.setText(reservation.getCustomerName());
            customerPhoneTextView.setText(reservation.getCustomerPhone());
            reservationTimeTextView.setText(reservation.getReservationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            numGuestsTextView.setText(String.valueOf(reservation.getNumGuests()));
            statusTextView.setText(reservation.getStatus());
        }
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
