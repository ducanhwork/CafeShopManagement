package com.group3.application.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.group3.application.R;
import com.group3.application.model.entity.User;

public class EditStaffActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText roleEditText;
    private EditText emailEditText;
    private EditText mobileEditText;
    private User staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_staff);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        staff = (User) getIntent().getSerializableExtra("staff");

        fullNameEditText = findViewById(R.id.fullNameEditText);
        roleEditText = findViewById(R.id.roleEditText);
        emailEditText = findViewById(R.id.emailEditText);
        mobileEditText = findViewById(R.id.mobileEditText);

        if (staff != null) {
            fullNameEditText.setText(staff.getFullname());
            roleEditText.setText(staff.getRole());
            emailEditText.setText(staff.getEmail());
            mobileEditText.setText(staff.getMobile());
        }

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveStaffInfo();
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void saveStaffInfo() {
        String fullName = fullNameEditText.getText().toString();
        String role = roleEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();

        staff.setFullname(fullName);
        staff.setRole(role);
        staff.setEmail(email);
        staff.setMobile(mobile);

        // Here you would typically make an API call to save the updated staff information

        finish(); // For now, just finish the activity
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
