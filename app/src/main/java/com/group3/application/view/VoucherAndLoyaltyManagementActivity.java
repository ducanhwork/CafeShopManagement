package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
// Thay đổi import từ MaterialButton sang MaterialCardView
import com.google.android.material.card.MaterialCardView;
import com.group3.application.R;

public class VoucherAndLoyaltyManagementActivity extends AppCompatActivity {

    public static final String ACTION_START_KEY = "ACTION_START";
    public static final String ACTION_CREATE_NEW = "CREATE_NEW_VOUCHER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_and_loyalty_management);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        MaterialCardView cardCreate = findViewById(R.id.card_create_voucher);
        MaterialCardView cardList   = findViewById(R.id.card_view_vouchers);
        MaterialCardView cardLoyaltyList = findViewById(R.id.card_loyalty_list);

        toolbar.setNavigationOnClickListener(v -> finish());

        cardLoyaltyList.setOnClickListener(v ->
            startActivity(new Intent(this, LoyaltyListActivity.class)));

        cardList.setOnClickListener(v -> {
            Intent i = new Intent(this, VoucherManagementActivity.class);
            startActivity(i);
        });

        cardCreate.setOnClickListener(v -> {
            Intent i = new Intent(this, VoucherManagementActivity.class);
            i.putExtra(ACTION_START_KEY, ACTION_CREATE_NEW);
            startActivity(i);
        });
    }
}
