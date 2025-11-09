package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.material.navigation.NavigationView;
import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.adapter.TableAdapter;
import com.group3.application.viewmodel.TableViewModel;

public class ReservationActivity extends AppCompatActivity implements TableAdapter.OnItemClick, NavigationView.OnNavigationItemSelectedListener {

    private TableViewModel viewModel;
    private TableAdapter tableAdapter;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        RecyclerView tablePanel = findViewById(R.id.table_panel);
        tablePanel.setLayoutManager(new LinearLayoutManager(this));

        tableAdapter = new TableAdapter(this);
        tablePanel.setAdapter(tableAdapter);

        viewModel = new ViewModelProvider(this).get(TableViewModel.class);

        viewModel.getTables().observe(this, tables -> {
            if (tables != null) {
                tableAdapter.setData(tables);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getLoading().observe(this, isLoading -> {
            // Show a progress bar or some other loading indicator
        });

        viewModel.loadTables(null, null);
    }

    @Override
    public void onClick(TableInfo table) {
        Intent intent = new Intent(this, ReservationListActivity.class);
        intent.putExtra("TABLE_ID", table.getId());
        startActivity(intent);
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

}
