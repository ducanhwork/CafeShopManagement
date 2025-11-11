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
import java.util.UUID;

public class EditStaffActivity extends AppCompatActivity {

    private StaffListViewModel staffListViewModel;
    private User staff;
    private List<Role> roles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_staff);

        staff = (User) getIntent().getSerializableExtra("staff");
        String staffIdExtra = getIntent().getStringExtra("staffId");

        // if the serializable User lost its id, set it from the explicit extra
        if (staff != null && staff.getId() == null && staffIdExtra != null) {
            try {
                staff.setId(UUID.fromString(staffIdExtra));
            } catch (IllegalArgumentException ignored) {
            }
        }

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
            // Do not set role text here yet because we may not have the roles list loaded to map id->name
        }

        staffListViewModel.getRoles().observe(this, new Observer<List<Role>>() {
            @Override
            public void onChanged(List<Role> roleList) {
                roles = roleList;
                ArrayAdapter<Role> adapter = new ArrayAdapter<>(EditStaffActivity.this, android.R.layout.simple_dropdown_item_1line, roles);
                roleAutoCompleteTextView.setAdapter(adapter);

                // If we have a staff object with a role id, display the matching role name in the AutoCompleteTextView
                if (staff != null && staff.getRole() != null) {
                    for (Role r : roles) {
                        if (r.getId().equals(staff.getRole())) {
                            roleAutoCompleteTextView.setText(r.getName());
                            break;
                        }
                    }
                }
            }
        });

        staffListViewModel.fetchRoles();

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (staff == null) return; // nothing to update

                String fullname = fullNameEditText.getText() != null ? fullNameEditText.getText().toString() : "";
                String email = emailEditText.getText() != null ? emailEditText.getText().toString() : "";
                String mobile = mobileEditText.getText() != null ? mobileEditText.getText().toString() : "";
                String password = passwordEditText.getText() != null ? passwordEditText.getText().toString() : "";

                staff.setFullname(fullname);
                staff.setEmail(email);
                staff.setMobile(mobile);
                staff.setPassword(password);

                String roleName = roleAutoCompleteTextView.getText() != null ? roleAutoCompleteTextView.getText().toString() : null;
                if (roles != null && roleName != null) {
                    for (Role role : roles) {
                        if (role.getName().equals(roleName)) {
                            // send role id (expected by backend), not role name
                            staff.setRole(role.getId());
                            break;
                        }
                    }
                }

                staffListViewModel.updateUser(staff);
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Staff");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
