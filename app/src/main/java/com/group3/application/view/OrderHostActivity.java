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

    public static final String EXTRA_EDIT_ORDER_ID = "EDIT_ORDER_ID";
    public static final String EXTRA_START_FRAGMENT = "START_FRAGMENT";

    private OrderViewModel orderVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_host);

        orderVM = new ViewModelProvider(this).get(OrderViewModel.class);

        String editOrderId = getIntent().getStringExtra(EXTRA_EDIT_ORDER_ID);
        if (editOrderId != null) {
            orderVM.loadExistingOrder(editOrderId);

            String startFragment = getIntent().getStringExtra(EXTRA_START_FRAGMENT);
            if ("PRODUCTS".equals(startFragment)) {
                navigateToProducts();
            } else {
                navigateToProducts();
            }
        } else {
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

    private void navigateToProducts(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_order, new ProductListFragment())
                .commit();
    }

    public void navigateToSummary() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_order, new OrderSummaryFragment())
                .addToBackStack(null)
                .commit();
    }
}
