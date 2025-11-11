
package com.group3.application.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.adapter.TableAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreateReservationActivity /*extends AppCompatActivity implements TableAdapter.OnTableClickListener*/ {

    /*private TableAdapter tableAdapter;
    private List<Table> tableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reservation);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        RecyclerView tablePanel = findViewById(R.id.table_panel);
        tablePanel.setLayoutManager(new GridLayoutManager(this, 4));

        initializeTableData();

        tableAdapter = new TableAdapter(tableList, this);
        tablePanel.setAdapter(tableAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_reservation_menu, menu);
        return true;
    }
i   @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            // In a real app, you would re-fetch table statuses from the backend
            Toast.makeText(this, "Refreshing table status...", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeTableData() {
        tableList = new ArrayList<>();
        tableList.add(new Table("1", "Table 1", 4, "Available"));
        tableList.add(new Table("2", "Table 2", 4, "Occupied"));
        tableList.add(new Table("3", "Table 3", 2, "Available"));
        tableList.add(new Table("4", "Table 4", 6, "Available"));
        tableList.add(new Table("5", "Table 5", 4, "Occupied"));
        tableList.add(new Table("6", "Table 6", 2, "Available"));
        tableList.add(new Table("7", "Table 7", 8, "Available"));
        tableList.add(new Table("8", "Table 8", 4, "Available"));
    }

    @Override
    public void onTableClick(Table table) {
        Intent intent = new Intent(CreateReservationActivity.this, ReservationListActivity.class);
        intent.putExtra("TABLE_NUMBER", table.getTableNumber());
        startActivity(intent);
    }*/
}
