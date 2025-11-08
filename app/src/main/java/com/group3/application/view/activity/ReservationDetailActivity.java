
package com.group3.application.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.group3.application.R;
import com.group3.application.model.entity.Reservation;

public class ReservationDetailActivity extends AppCompatActivity {

    private Reservation reservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        reservation = (Reservation) getIntent().getSerializableExtra("RESERVATION");

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (reservation != null) {
            populateDetails();
        }

        Button editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(v -> {
            // Placeholder for edit functionality
            Toast.makeText(this, "Edit feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        Button cancelButton = findViewById(R.id.cancel_reservation_button);
        cancelButton.setOnClickListener(v -> showCancelConfirmationDialog());
    }

    private void populateDetails() {
        TextView customerName = findViewById(R.id.detail_customer_name);
        Chip statusChip = findViewById(R.id.detail_status_chip);
        TextView time = findViewById(R.id.detail_time);
        TextView date = findViewById(R.id.detail_date);
        TextView pax = findViewById(R.id.detail_pax);
        TextView table = findViewById(R.id.detail_table);

        customerName.setText(reservation.getCustomerName());
        statusChip.setText(reservation.getStatus());
        time.setText(reservation.getTime());
        date.setText(reservation.getDate());
        pax.setText(String.valueOf(reservation.getPax()) + " Pax");
        table.setText("Table " + reservation.getTableNumber());

        // Set chip color based on status
        ColorStateList chipColor = ColorStateList.valueOf(Color.GRAY); // Default
        switch (reservation.getStatus()) {
            case "Confirmed":
                chipColor = ColorStateList.valueOf(Color.parseColor("#4CAF50")); // Green
                break;
            case "Pending":
                chipColor = ColorStateList.valueOf(Color.parseColor("#FFC107")); // Amber
                break;
            case "Canceled":
                chipColor = ColorStateList.valueOf(Color.parseColor("#F44336")); // Red
                break;
        }
        statusChip.setChipBackgroundColor(chipColor);
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    reservation.setStatus("Canceled");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_RESERVATION", reservation);
                    setResult(RESULT_OK, resultIntent);
                    Toast.makeText(this, "Reservation Canceled", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
