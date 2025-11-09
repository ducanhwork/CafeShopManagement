package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
// SỬA 1: Xóa import
// import androidx.activity.result.ActivityResultLauncher;
// import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.group3.application.R;
import com.google.android.material.button.MaterialButton;
import android.view.View; // Thêm import này

public class HomeMenuActivity extends AppCompatActivity {

    public static final String ACTION_START_KEY = "ACTION_START";
    public static final String ACTION_CREATE_NEW = "CREATE_NEW_VOUCHER";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        MaterialButton btnCreate = findViewById(R.id.btnCreateVoucher);
        MaterialButton btnList   = findViewById(R.id.btnViewList);
        MaterialButton btnLoyaltyList = findViewById(R.id.btnLoyaltyList);

        btnLoyaltyList.setOnClickListener(v ->
            startActivity(new Intent(this, LoyaltyListActivity.class)));

        btnList.setOnClickListener(v -> {
            Intent i = new Intent(this, VoucherManagementActivity.class);
            startActivity(i);
        });

        btnCreate.setOnClickListener(v -> {
            Intent i = new Intent(this, VoucherManagementActivity.class);
            i.putExtra(ACTION_START_KEY, ACTION_CREATE_NEW);
            startActivity(i);
        });

    }
}
