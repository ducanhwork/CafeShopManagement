package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group3.application.R;
import com.group3.application.model.entity.User;
import com.group3.application.viewmodel.StaffListViewModel;

public class StaffDetailActivity extends AppCompatActivity {

    private StaffListViewModel staffListViewModel;

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

        staffListViewModel = new ViewModelProvider(this).get(StaffListViewModel.class);

        if (staff != null) {
            TextView fullNameTextView = findViewById(R.id.fullNameTextView);
            TextView roleTextView = findViewById(R.id.roleTextView);
            TextView emailTextView = findViewById(R.id.emailTextView);
            TextView mobileTextView = findViewById(R.id.mobileTextView);

            fullNameTextView.setText(staff.getFullname());
            if (staff.getRole() != null) {
                roleTextView.setText(staff.getRole().toString());
            }
            emailTextView.setText(staff.getEmail());
            mobileTextView.setText(staff.getMobile());
        }

        FloatingActionButton editStaffButton = findViewById(R.id.editStaffButton);
        editStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDetailActivity.this, EditStaffActivity.class);
                intent.putExtra("staff", staff);
                // also send the id explicitly as a string to avoid cases where Serializable may not carry it
                if (staff != null && staff.getId() != null) {
                    intent.putExtra("staffId", staff.getId().toString());
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
