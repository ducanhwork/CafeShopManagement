package com.group3.application.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.group3.application.R;
import com.group3.application.view.fragment.OrderSummaryFragment;
import com.group3.application.view.fragment.ProductListFragment;
import com.group3.application.viewmodel.OrderViewModel;

import java.util.ArrayList;

public class OrderHostActivity extends AppCompatActivity {

    private OrderViewModel orderVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_host);

        // 1. Lấy thông tin bàn từ Màn 1
        ArrayList<String> tableIds = getIntent().getStringArrayListExtra("tableIds");
        // 2. Lấy CHUỖI TÊN (đã được nối từ màn trước)
        String tableNames = getIntent().getStringExtra("tableNames");

        // 2. Khởi tạo Shared ViewModel (scope của Activity này)
        orderVM = new ViewModelProvider(this).get(OrderViewModel.class);

        // 3. Thiết lập thông tin bàn cho ViewModel
        orderVM.setTableInfo(tableIds, tableNames);

        // 4. Load Fragment đầu tiên (Chọn Món)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_order, new ProductListFragment())
                    .commit();
        }
    }

    // 5. Hàm để chuyển sang Màn 3 (Tóm tắt)
    public void navigateToSummary() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_order, new OrderSummaryFragment())
                .addToBackStack(null) // Cho phép nhấn Back để quay lại
                .commit();
    }
}