
package com.group3.application.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group3.application.R;
import com.group3.application.model.entity.Reservation;
import com.group3.application.model.repository.ReservationRepository;
import com.group3.application.view.adapter.ReservationAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationListActivity extends AppCompatActivity implements ReservationAdapter.OnItemClickListener {

    private ReservationAdapter reservationAdapter;
    private List<Reservation> currentReservations;
    private String tableNumber;
    private ReservationRepository repository;

    private final ActivityResultLauncher<Intent> detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Reservation updatedReservation = (Reservation) result.getData().getSerializableExtra("UPDATED_RESERVATION");
                    if (updatedReservation != null) {
                        // Update the data in the central repository
                        repository.updateReservation(updatedReservation);
                        // Reload the data from the single source of truth
                        loadReservations();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);

        repository = ReservationRepository.getInstance();
        tableNumber = getIntent().getStringExtra("TABLE_NUMBER");

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitle("Reservations for Table " + tableNumber);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView reservationsRecyclerView = findViewById(R.id.reservations_recycler_view);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initial data load
        currentReservations = repository.getReservationsForTable(tableNumber);
        reservationAdapter = new ReservationAdapter(new ArrayList<>(currentReservations), this);
        reservationsRecyclerView.setAdapter(reservationAdapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_reservation);
        fab.setOnClickListener(v -> showCreateReservationDialog());

        ChipGroup filterGroup = findViewById(R.id.status_filter_group);
        filterGroup.setOnCheckedChangeListener((group, checkedId) -> filterAndDisplay());
        // Set default selection
        filterGroup.check(R.id.chip_all);
    }

    private void loadReservations() {
        currentReservations = repository.getReservationsForTable(tableNumber);
        filterAndDisplay();
    }

    private void filterAndDisplay() {
        ChipGroup filterGroup = findViewById(R.id.status_filter_group);
        int checkedId = filterGroup.getCheckedChipId();

        if (checkedId == R.id.chip_all) {
            reservationAdapter.filterList(new ArrayList<>(currentReservations));
        } else if (checkedId == R.id.chip_pending) {
            filterByStatus("Pending");
        } else if (checkedId == R.id.chip_confirmed) {
            filterByStatus("Confirmed");
        } else if (checkedId == R.id.chip_canceled) {
            filterByStatus("Canceled");
        }
    }

    @Override
    public void onItemClick(Reservation reservation) {
        Intent intent = new Intent(this, ReservationDetailActivity.class);
        intent.putExtra("RESERVATION", reservation);
        detailLauncher.launch(intent);
    }

    private void filterByStatus(String status) {
        List<Reservation> filteredList = currentReservations.stream()
                .filter(r -> status.equals(r.getStatus()))
                .collect(Collectors.toList());
        reservationAdapter.filterList(filteredList);
    }

    private void showCreateReservationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_reservation, null);
        builder.setView(dialogView);

        final EditText nameInput = dialogView.findViewById(R.id.name_input);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.confirm_button).setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            Reservation newReservation = new Reservation(name, "14:00", "Pending", "26/10/2025", 2, tableNumber);
            
            // Add data to the central repository
            repository.addReservation(newReservation);
            // Reload the data from the single source of truth
            loadReservations();

            dialog.dismiss();
            Toast.makeText(this, "Reservation Created", Toast.LENGTH_SHORT).show();
        });

        dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
