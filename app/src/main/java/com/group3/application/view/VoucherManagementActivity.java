package com.group3.application.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // SỬA 1: Thêm import
import androidx.fragment.app.Fragment;

import com.group3.application.R;
import com.group3.application.view.fragment.CreateVoucherFragment;
import com.group3.application.view.fragment.VoucherListFragment; // SỬA 2: Thêm import

public class VoucherManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {

            String action = getIntent().getStringExtra(HomeMenuActivity.ACTION_START_KEY);

            Fragment initialFragment;
            if (HomeMenuActivity.ACTION_CREATE_NEW.equals(action)) {
                initialFragment = new CreateVoucherFragment();
                getSupportActionBar().setTitle("Tạo Voucher mới");
            } else {
                initialFragment = new VoucherListFragment();
            }

            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_voucher, initialFragment)
                .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
