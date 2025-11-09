package com.group3.application.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.group3.application.R;
import com.group3.application.model.entity.User;

public class StaffDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        User staff = (User) getIntent().getSerializableExtra("staff");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (staff != null) {
                getSupportActionBar().setTitle(staff.getFullname());
            } else {
                getSupportActionBar().setTitle("Staff Detail");
            }
        }

        if (staff != null) {
            TextView fullNameTextView = findViewById(R.id.fullNameTextView);
            TextView roleTextView = findViewById(R.id.roleTextView);
            TextView emailTextView = findViewById(R.id.emailTextView);
            TextView mobileTextView = findViewById(R.id.mobileTextView);

            fullNameTextView.setText(staff.getFullname());
            roleTextView.setText(staff.getRole());
            emailTextView.setText(staff.getEmail());
            mobileTextView.setText(staff.getMobile());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
