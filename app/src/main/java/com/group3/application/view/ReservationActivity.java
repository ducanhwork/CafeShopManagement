package com.group3.application.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.adapter.TableAdapter;
import com.group3.application.viewmodel.ReservationViewModel;

public class ReservationActivity /*extends AppCompatActivity implements TableAdapter.OnItemClick*/ {

    /*private ReservationViewModel viewModel;
    private TableAdapter tableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        RecyclerView tablePanel = findViewById(R.id.table_panel);
        tablePanel.setLayoutManager(new LinearLayoutManager(this));

        tableAdapter = new TableAdapter(this);
        tablePanel.setAdapter(tableAdapter);

        viewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

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

        viewModel.getIsLoading().observe(this, isLoading -> {
            // Show a progress bar or some other loading indicator
        });

        viewModel.fetchTables();
    }

    @Override
    public void onClick(TableInfo table) {
        Toast.makeText(this, "Clicked: " + table.getName(), Toast.LENGTH_SHORT).show();
    }*/
}
