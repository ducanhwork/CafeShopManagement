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

    // SỬA: Thêm các hằng số để nhận dữ liệu từ Intent
    public static final String EXTRA_EDIT_ORDER_ID = "EDIT_ORDER_ID";
    public static final String EXTRA_START_FRAGMENT = "START_FRAGMENT";

    private OrderViewModel orderVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_host);

        orderVM = new ViewModelProvider(this).get(OrderViewModel.class);

        // SỬA: Logic để xử lý giữa TẠO MỚI và CHỈNH SỬA đơn hàng
        String editOrderId = getIntent().getStringExtra(EXTRA_EDIT_ORDER_ID);
        if (editOrderId != null) {
            // --- CHẾ ĐỘ CHỈNH SỬA ---
            // Yêu cầu ViewModel tải các món ăn của đơn hàng cũ
            // Chú ý: Cần thêm hàm `loadExistingOrder` vào OrderViewModel
            orderVM.loadExistingOrder(editOrderId);

            // Kiểm tra xem có yêu cầu mở một fragment cụ thể không
            String startFragment = getIntent().getStringExtra(EXTRA_START_FRAGMENT);
            if ("PRODUCTS".equals(startFragment)) {
                navigateToProducts();
            } else {
                // Mặc định, có thể làm gì đó khác nếu cần
                navigateToProducts(); // Hiện tại vẫn mở màn hình sản phẩm
            }
        } else {
            // --- CHẾ ĐỘ TẠO MỚI (Logic cũ) ---
            ArrayList<String> tableIds = getIntent().getStringArrayListExtra("tableIds");
            String tableNames = getIntent().getStringExtra("tableNames");
            orderVM.setTableInfo(tableIds, tableNames);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_order, new ProductListFragment())
                        .commit();
            }
        }
    }

    // Hàm để chuyển sang Màn 2 (Chọn Món)
    private void navigateToProducts(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_order, new ProductListFragment())
                .commit();
    }

    // Hàm để chuyển sang Màn 3 (Tóm tắt)
    public void navigateToSummary() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_order, new OrderSummaryFragment())
                .addToBackStack(null)
                .commit();
    }
}
