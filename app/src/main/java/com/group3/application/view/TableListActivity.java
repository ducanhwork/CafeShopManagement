package com.group3.application.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.adapter.TableAdapter;
import com.group3.application.viewmodel.TableViewModel;

public class TableListActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private EditText edtSearch;
    private TableAdapter adapter;

    private TableViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        swipe = findViewById(R.id.swipe);
        rv = findViewById(R.id.rvTables);
        edtSearch = findViewById(R.id.edtSearch);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        View appBar = findViewById(R.id.appBar);
        ViewCompat.setOnApplyWindowInsetsListener(appBar, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        adapter = new TableAdapter(item -> vm.onTableClicked(item));

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(TableViewModel.class);

        vm.getTables().observe(this, list -> adapter.setData(list));
        vm.getLoading().observe(this, isLoading -> swipe.setRefreshing(Boolean.TRUE.equals(isLoading)));
        vm.getError().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        swipe.setRefreshing(true);
        vm.loadTables(null, null);

        swipe.setOnRefreshListener(() ->
                vm.loadTables(null, edtSearch.getText() == null ? null : edtSearch.getText().toString().trim())
        );

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                vm.loadTables(null, s.toString().trim());
            }
        });

//        vm.getEvents().observe(this, ev -> {
//            var pair = ev.getIfNotHandled();
//            if(pair == null) return;
//            TableViewModel.TableAction action = pair.first;
//            TableInfo table = pair.second;
//
//            switch (action){
//                case SHOW_CONFIRM_RESERVED:
//                    new AlertDialog.Builder(this)
//                            .setTitle("Bàn đã đặt chỗ")
//                            .setMessage("Bàn " + table.getName() + " đang ở trạng thái ĐẶT CHỖ.\nBạn có muốn tiếp tục không?")
//                            .setNegativeButton("Hủy", null)
//                            .setPositiveButton("Tiếp tục", (d,w) -> vm.proceedReserved(table))
//                            .show();
//                    break;
//
//                case OPEN_ORDER:
//                    openOrderScreen(table.getId(), table.getName());
//                    break;
//
//                case SHOW_ERROR:
//                    Toast.makeText(this, "Trạng thái bàn không hỗ trợ", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        });
    }

    private void openOrderScreen(String tableId, String tableName) {
        /*Intent i = new Intent(this, OrderActivity.class);
        i.putExtra("tableId", tableId);
        i.putExtra("tableName", tableName);
        startActivity(i);*/
        Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
    }
}