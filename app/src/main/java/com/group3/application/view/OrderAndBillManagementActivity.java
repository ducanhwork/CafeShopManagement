package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.group3.application.R;

public class OrderAndBillManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_and_bill_management);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        MaterialCardView cardManageOrders = findViewById(R.id.card_manage_orders);
        MaterialCardView cardManageBills = findViewById(R.id.card_manage_bills);

        toolbar.setNavigationOnClickListener(v -> finish());

        cardManageOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderListActivity.class);
            startActivity(intent);
        });

        cardManageBills.setOnClickListener(v -> {
            Intent intent = new Intent(this, BillListActivity.class);
            startActivity(intent);
        });
    }
}
