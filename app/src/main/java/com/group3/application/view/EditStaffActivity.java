package com.group3.application.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.model.entity.Role;
import com.group3.application.model.entity.User;
import com.group3.application.viewmodel.StaffListViewModel;

import java.util.List;

public class EditStaffActivity extends AppCompatActivity {

    private StaffListViewModel staffListViewModel;
    private User staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_staff);

        staff = (User) getIntent().getSerializableExtra("staff");

        setupToolbar();

        staffListViewModel = new ViewModelProvider(this).get(StaffListViewModel.class);

        TextInputEditText fullNameEditText = findViewById(R.id.fullNameEditText);
        TextInputEditText emailEditText = findViewById(R.id.emailEditText);
        TextInputEditText mobileEditText = findViewById(R.id.mobileEditText);
        TextInputEditText passwordEditText = findViewById(R.id.passwordEditText);
        AutoCompleteTextView roleAutoCompleteTextView = findViewById(R.id.roleAutoCompleteTextView);

        if (staff != null) {
            fullNameEditText.setText(staff.getFullname());
            emailEditText.setText(staff.getEmail());
            mobileEditText.setText(staff.getMobile());
            passwordEditText.setText(staff.getPassword());
            roleAutoCompleteTextView.setText(staff.getRole());
        }

        staffListViewModel.getRoles().observe(this, new Observer<List<Role>>() {
            @Override
            public void onChanged(List<Role> roles) {
                ArrayAdapter<Role> adapter = new ArrayAdapter<>(EditStaffActivity.this, android.R.layout.simple_dropdown_item_1line, roles);
                roleAutoCompleteTextView.setAdapter(adapter);
            }
        });

        staffListViewModel.fetchRoles();

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                staff.setFullname(fullNameEditText.getText().toString());
                staff.setEmail(emailEditText.getText().toString());
                staff.setMobile(mobileEditText.getText().toString());
                staff.setPassword(passwordEditText.getText().toString());
                staff.setRole(roleAutoCompleteTextView.getText().toString());

                staffListViewModel.updateUser(staff);
                finish();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reservations");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
