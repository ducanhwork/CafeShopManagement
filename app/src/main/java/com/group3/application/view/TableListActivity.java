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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.adapter.TableAdapter;
import com.group3.application.viewmodel.TableViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableListActivity extends AppCompatActivity {

    public static final String EXTRA_INITIAL_TABLE_IDS = "EXTRA_INITIAL_TABLE_IDS";

    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private EditText edtSearch;
    private TableAdapter adapter;
    private ExtendedFloatingActionButton fabConfirm;

    private TableViewModel vm;
    private boolean isEditMode = false;
    private boolean initialSelectionApplied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        swipe = findViewById(R.id.swipe);
        rv = findViewById(R.id.rvTables);
        edtSearch = findViewById(R.id.edtSearch);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        View appBar = findViewById(R.id.appBar);
        ViewCompat.setOnApplyWindowInsetsListener(appBar, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        fabConfirm = findViewById(R.id.fabConfirmTables);

        if (getIntent().hasExtra(EXTRA_INITIAL_TABLE_IDS)) {
            isEditMode = true;
            getSupportActionBar().setTitle("Chỉnh sửa bàn");
        } else {
            getSupportActionBar().setTitle("Tạo đơn hàng");
        }

        adapter = new TableAdapter(item -> vm.onTableClicked(item));
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(TableViewModel.class);

        vm.getTables().observe(this, list -> {
            adapter.setData(list);
            if (isEditMode && !initialSelectionApplied && list != null && !list.isEmpty()) {
                ArrayList<String> initialIds = getIntent().getStringArrayListExtra(EXTRA_INITIAL_TABLE_IDS);
                vm.selectTablesByIds(initialIds);
                initialSelectionApplied = true;
            }
        });

        vm.getLoading().observe(this, isLoading -> swipe.setRefreshing(Boolean.TRUE.equals(isLoading)));
        vm.getError().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        vm.getSelectedTables().observe(this, selectedList -> {
            adapter.setSelectedTables(selectedList);
            if (selectedList == null || selectedList.isEmpty()) {
                fabConfirm.hide();
            } else {
                fabConfirm.setText("Xác nhận (" + selectedList.size() + ")");
                fabConfirm.show();
            }
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

        // SỬA: Xử lý logic trả về kết quả cho chế độ sửa
        fabConfirm.setOnClickListener(v -> {
            if (isEditMode) {
                List<TableInfo> selectedTables = vm.getSelectedTables().getValue();
                if (selectedTables == null) {
                    selectedTables = new ArrayList<>();
                }
                ArrayList<String> updatedTableIds = new ArrayList<>();
                for (TableInfo table : selectedTables) {
                    updatedTableIds.add(table.getId());
                }

                Intent resultIntent = new Intent();
                resultIntent.putStringArrayListExtra("updatedTableIds", updatedTableIds);
                setResult(AppCompatActivity.RESULT_OK, resultIntent);
                finish();
            } else {
                vm.confirmSelection();
            }
        });

        vm.getEvents().observe(this, ev -> {
            var pair = ev.getIfNotHandled();
            if (pair == null) return;
            TableViewModel.TableAction action = pair.first;
            List<TableInfo> tables = pair.second;

            switch (action) {
                case SHOW_CONFIRM_RESERVED:
                    String reservedNames = tables.stream()
                            .filter(t -> "RESERVED".equalsIgnoreCase(t.getStatus()))
                            .map(TableInfo::getName)
                            .collect(Collectors.joining(", "));

                    new AlertDialog.Builder(this)
                            .setTitle("Bàn đã đặt chỗ")
                            .setMessage("Các bàn sau đang ở trạng thái ĐẶT CHỖ: " + reservedNames + ".\nBạn có muốn tiếp tục không?")
                            .setNegativeButton("Hủy", null)
                            .setPositiveButton("Tiếp tục", (d, w) -> vm.proceedReserved(tables))
                            .show();
                    break;

                case OPEN_ORDER:
                    openOrderScreen(tables);
                    break;

                case SHOW_ERROR_SINGLE:
                    TableInfo table = tables.get(0);
                    Toast.makeText(this, "Bàn " + table.getName() + " (" + table.getStatus() + ") không thể chọn.", Toast.LENGTH_SHORT).show();
                    break;

                case SHOW_ERROR_MULTI:
                    Toast.makeText(this, "Bạn chưa chọn bàn nào.", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void openOrderScreen(List<TableInfo> tables) {
        Intent i = new Intent(this, OrderHostActivity.class);
        ArrayList<String> tableIds = new ArrayList<>();
        ArrayList<String> tableNamesList = new ArrayList<>();
        for (TableInfo table : tables) {
            tableIds.add(table.getId());
            tableNamesList.add(table.getName());
        }
        i.putStringArrayListExtra("tableIds", tableIds);
        String combinedNames = String.join(", ", tableNamesList);
        i.putExtra("tableNames", combinedNames);
        startActivity(i);
    }
}
