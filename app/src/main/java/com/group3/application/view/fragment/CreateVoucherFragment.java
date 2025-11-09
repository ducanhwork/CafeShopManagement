package com.group3.application.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.model.bean.VoucherRequest;
import com.group3.application.viewmodel.VoucherViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CreateVoucherFragment extends Fragment {

    private TextInputLayout tilCode, tilValue, tilStart, tilEnd, tilType, tilStatus;
    private TextInputEditText etCode, etValue, etStart, etEnd;
    private MaterialAutoCompleteTextView actType, actStatus;
    private View btnSave, progress;

    private VoucherViewModel viewModel;
    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ymd.setTimeZone(TimeZone.getTimeZone("UTC"));

        viewModel = new ViewModelProvider(requireActivity()).get(VoucherViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupDropdowns();
        observeState();

        etStart.setOnClickListener(v -> pickDate(etStart));
        etEnd.setOnClickListener(v -> pickDate(etEnd));

        etCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etCode.getText() != null) {
            }
        });

        btnSave.setOnClickListener(v -> {
            clearErrors();
            VoucherRequest req = buildRequest();

            viewModel.createVoucher(req);
        });

    }

    private void bindViews(View view) {
        tilCode = view.findViewById(R.id.tilCode);
        tilValue = view.findViewById(R.id.tilValue);
        tilStart = view.findViewById(R.id.tilStart);
        tilEnd = view.findViewById(R.id.tilEnd);
        tilType = view.findViewById(R.id.tilType);
        tilStatus = view.findViewById(R.id.tilStatus);

        etCode = view.findViewById(R.id.etCode);
        etValue = view.findViewById(R.id.etValue);
        etStart = view.findViewById(R.id.etStart);
        etEnd = view.findViewById(R.id.etEnd);

        actType = view.findViewById(R.id.actType);
        actStatus = view.findViewById(R.id.actStatus);

        btnSave = view.findViewById(R.id.btnSave);
        progress = view.findViewById(R.id.progress);
    }

    private void setupDropdowns() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_list_item_1,
            getResources().getStringArray(R.array.voucher_types)
        );
        actType.setAdapter(typeAdapter);
        if (TextUtils.isEmpty(actType.getText())) actType.setText("PERCENT", false);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_list_item_1,
            getResources().getStringArray(R.array.voucher_statuses)
        );
        actStatus.setAdapter(statusAdapter);
        if (TextUtils.isEmpty(actStatus.getText())) actStatus.setText("ACTIVE", false);
    }

    private void pickDate(TextInputEditText target) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Chọn ngày")
            .build();
        picker.addOnPositiveButtonClickListener(sel -> {
            Date d = new Date(sel);
            target.setText(ymd.format(d));
        });
        picker.show(getParentFragmentManager(), "date");
    }

    private void observeState() {
        viewModel.saveState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            switch (state.status) {
                case SAVING:
                    progress.setVisibility(View.VISIBLE);
                    btnSave.setEnabled(false);
                    break;
                case SUCCESS:
                    progress.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Tạo thành công!", Toast.LENGTH_SHORT).show();
                    clearFormFields();
                    viewModel.clearSaveState();
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                    break;
                case ERROR:
                    progress.setVisibility(View.GONE);
                    btnSave.setEnabled(true);

                    Toast.makeText(getContext(), state.message, Toast.LENGTH_LONG).show();

                    viewModel.clearSaveState();
                    break;
                default:
                    progress.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
            }
        });
    }

    private VoucherRequest buildRequest() {
        VoucherRequest r = new VoucherRequest();
        r.code = getText(etCode);
        r.type = getText(actType);
        String valueStr = getText(etValue).replace(",", "").trim();

        if (!TextUtils.isEmpty(valueStr)) {
            try {
                r.value = Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                r.value = Double.NaN;
            }
        }
        r.startDate = getText(etStart);
        r.endDate = getText(etEnd);
        r.status = getText(actStatus);
        return r;
    }

    private void clearErrors() {
        tilCode.setError(null);
        tilValue.setError(null);
        tilStart.setError(null);
        tilEnd.setError(null);
        tilType.setError(null);
        tilStatus.setError(null);
    }

    private void clearFormFields() {
        etCode.setText("");
        etValue.setText("");
        etStart.setText("");
        etEnd.setText("");
        actType.setText("", false);
        actStatus.setText("", false);

        clearErrors();
    }
    private static String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
    private static String getText(MaterialAutoCompleteTextView v) {
        return v.getText() == null ? "" : v.getText().toString().trim();
    }
    private static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.clearSaveState();
    }
}
