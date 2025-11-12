package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group3.application.R;
import com.group3.application.model.entity.Role;
import com.group3.application.model.entity.User;
import com.group3.application.viewmodel.StaffListViewModel;

import java.util.List;

public class StaffDetailActivity extends AppCompatActivity {

    private static final int EDIT_STAFF_REQUEST = 1;
    private StaffListViewModel staffListViewModel;
    private User staff;

    private TextView fullNameTextView;
    private TextView roleTextView;
    private TextView emailTextView;
    private TextView mobileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        staff = (User) getIntent().getSerializableExtra("staff");

        fullNameTextView = findViewById(R.id.fullNameTextView);
        roleTextView = findViewById(R.id.roleTextView);
        emailTextView = findViewById(R.id.emailTextView);
        mobileTextView = findViewById(R.id.mobileTextView);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        staffListViewModel = new ViewModelProvider(this).get(StaffListViewModel.class);
        staffListViewModel.fetchRoles();

        updateUI();

        FloatingActionButton editStaffButton = findViewById(R.id.editStaffButton);
        editStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDetailActivity.this, EditStaffActivity.class);
                intent.putExtra("staff", staff);
                if (staff != null && staff.getId() != null) {
                    intent.putExtra("staffId", staff.getId().toString());
                }
                startActivityForResult(intent, EDIT_STAFF_REQUEST);
            }
        });
    }

    private void updateUI() {
        if (staff != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(staff.getFullname());
            }
            fullNameTextView.setText(staff.getFullname());
            emailTextView.setText(staff.getEmail());
            mobileTextView.setText(staff.getMobile());
            if (staff.getRole() == null) {
                roleTextView.setText("N/A");
            } else {
                staffListViewModel.getRoles().observe(this, new Observer<List<Role>>() {
                    @Override
                    public void onChanged(List<Role> roles) {
                        if (roles != null) {
                            for (Role role : roles) {
                                if (role.getName().equalsIgnoreCase(staff.getRole())) {
                                    roleTextView.setText(role.getName());
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_STAFF_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("updatedStaff")) {
                staff = (User) data.getSerializableExtra("updatedStaff");
                updateUI();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
