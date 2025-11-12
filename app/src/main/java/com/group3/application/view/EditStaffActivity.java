package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.model.dto.UserUpdateRequest;
import com.group3.application.model.entity.Role;
import com.group3.application.model.entity.User;
import com.group3.application.viewmodel.StaffListViewModel;

import java.util.List;
import java.util.UUID;

public class EditStaffActivity extends AppCompatActivity {

    private StaffListViewModel staffListViewModel;
    private UserUpdateRequest staff;
    private List<Role> roles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_staff);

        User user = (User) getIntent().getSerializableExtra("staff");
        if (user != null) {
            staff = new UserUpdateRequest(user.getId().toString(), user.getEmail(), user.getFullname(), user.getMobile(), null);
        }

        String staffIdExtra = getIntent().getStringExtra("staffId");

        // if the serializable User lost its id, set it from the explicit extra
        if (staff != null && staff.getId() == null && staffIdExtra != null) {
            try {
                staff.setId(staffIdExtra);
            } catch (IllegalArgumentException ignored) {
            }
        }

        setupToolbar();

        staffListViewModel = new ViewModelProvider(this).get(StaffListViewModel.class);

        TextInputEditText fullNameEditText = findViewById(R.id.fullNameEditText);
        TextInputEditText emailEditText = findViewById(R.id.emailEditText);
        TextInputEditText mobileEditText = findViewById(R.id.mobileEditText);
        AutoCompleteTextView roleAutoCompleteTextView = findViewById(R.id.roleAutoCompleteTextView);

        if (staff != null) {
            fullNameEditText.setText(staff.getFullname());
            emailEditText.setText(staff.getEmail());
            mobileEditText.setText(staff.getMobile());
            // Do not set role text here yet because we may not have the roles list loaded to map id->name
        }

        staffListViewModel.getRoles().observe(this, new Observer<List<Role>>() {
            @Override
            public void onChanged(List<Role> roleList) {
                roles = roleList;
                ArrayAdapter<Role> adapter = new ArrayAdapter<>(EditStaffActivity.this, android.R.layout.simple_dropdown_item_1line, roles);
                roleAutoCompleteTextView.setAdapter(adapter);

                // If we have a user object with a role name, find the role and set the id and text
                if (user != null && user.getRole() != null) {
                    for (Role r : roles) {
                        if (r.getName().equals(user.getRole())) {
                            staff.setRoleId(r.getId());
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

                staff.setFullname(fullname);
                staff.setEmail(email);
                staff.setMobile(mobile);

                String roleName = roleAutoCompleteTextView.getText() != null ? roleAutoCompleteTextView.getText().toString() : null;
                if (roles != null && roleName != null) {
                    for (Role role : roles) {
                        if (role.getName().equals(roleName)) {
                            // send role id (expected by backend), not role name
                            staff.setRoleId(role.getId());
                            break;
                        }
                    }
                }

                Log.e("Id", staff.getId().toString());
                Log.e("Email", staff.getEmail());
                Log.e("Name", staff.getFullname());
                Log.e("Mobile", staff.getMobile());
                Log.e("Role", staff.getRoleId());

                staffListViewModel.updateUser(staff);
            }
        });

        staffListViewModel.getUpdatedStaff().observe(this, new Observer<UserUpdateRequest>() {
            @Override
            public void onChanged(UserUpdateRequest updatedStaff) {
                if (updatedStaff != null) {
                    Toast.makeText(EditStaffActivity.this, "Staff updated successfully", Toast.LENGTH_SHORT).show();

                    String roleName = "";
                    if (roles != null && updatedStaff.getRoleId() != null) {
                        for (Role role : roles) {
                            if (role.getId().equals(updatedStaff.getRoleId())) {
                                roleName = role.getName();
                                break;
                            }
                        }
                    }
                    User updatedUser = new User(UUID.fromString(updatedStaff.getId()), updatedStaff.getEmail(), null, updatedStaff.getFullname(), updatedStaff.getMobile(), roleName);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedStaff", updatedUser);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
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
