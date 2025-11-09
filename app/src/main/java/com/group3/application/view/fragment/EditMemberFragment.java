package com.group3.application.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.common.util.HttpException;
import com.group3.application.model.bean.UpdateLoyaltyMemberRequest;
import com.group3.application.viewmodel.LoyaltyViewModel;

import java.util.UUID;

public class EditMemberFragment extends Fragment {

    private static final String ARG_ID = "ARG_ID";
    private static final String ARG_NAME = "ARG_NAME";
    private static final String ARG_PHONE = "ARG_PHONE";
    private static final String ARG_EMAIL = "ARG_EMAIL";

    private LoyaltyViewModel viewModel;
    private UUID customerId;

    private TextInputEditText etName, etPhone, etEmail;
    private Button btnSave;
    private ProgressBar progressBar;

    public static EditMemberFragment newInstance(UUID id, String name, String phone, String email) {
        EditMemberFragment fragment = new EditMemberFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putString(ARG_PHONE, phone);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LoyaltyViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.et_full_name);
        etPhone = view.findViewById(R.id.et_phone);
        etEmail = view.findViewById(R.id.et_email);
        btnSave = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progress_bar);

        if (getArguments() != null) {
            customerId = (UUID) getArguments().getSerializable(ARG_ID);
            etName.setText(getArguments().getString(ARG_NAME));
            etPhone.setText(getArguments().getString(ARG_PHONE));
            etEmail.setText(getArguments().getString(ARG_EMAIL));
        }

        setActivityTitle("Chỉnh sửa: " + etName.getText().toString());

        btnSave.setOnClickListener(v -> attemptSave());

        observeViewModel();
    }

    private void attemptSave() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Tên không được để trống");
            return;
        }
        if (phone.isEmpty()) {
            etPhone.setError("SĐT không được để trống");
            return;
        }

        UpdateLoyaltyMemberRequest request = new UpdateLoyaltyMemberRequest.Builder()
            .setFullName(name)
            .setPhone(phone)
            .setEmail(email.isEmpty() ? null : email)
            .build();

        viewModel.updateMember(customerId, request);
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSave.setEnabled(!isLoading);
        });

        viewModel.updateResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.isSuccess()) {
                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            } else {
                String error = "Lỗi không xác định";
                if (result.error instanceof HttpException) {
                    HttpException httpError = (HttpException) result.error;
                    if (httpError.code == 400 || httpError.code == 409) {
                        error = "Lỗi: SĐT hoặc Email có thể đã tồn tại.";
                    } else {
                        error = "Lỗi " + httpError.code;
                    }
                }
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();

                viewModel.clearUpdateResult();
            }
        });
    }

    private void setActivityTitle(String title) {
        if (getActivity() instanceof AppCompatActivity && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setActivityTitle("Khách hàng thân thiết");
        viewModel.clearUpdateResult();
    }
}
