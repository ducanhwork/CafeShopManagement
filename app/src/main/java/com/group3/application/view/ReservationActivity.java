package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.adapter.TableAdapter;
import com.group3.application.viewmodel.TableViewModel;

public class ReservationActivity extends BaseDrawerActivity implements TableAdapter.OnItemClick {

    private TableViewModel viewModel;
    private TableAdapter tableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

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
}
