package com.group3.application.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group3.application.R;
import com.group3.application.view.adapter.ReservationListAdapter;
import com.group3.application.viewmodel.ReservationViewModel;

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

        viewModel.fetchReservationsByTable(tableId);

        FloatingActionButton fabAddReservation = findViewById(R.id.fab_add_reservation);
        fabAddReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ReservationListActivity.this, "Add new reservation clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
