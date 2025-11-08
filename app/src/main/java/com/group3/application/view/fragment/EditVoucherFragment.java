package com.group3.application.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
import com.group3.application.model.bean.VoucherResponse;
import com.group3.application.viewmodel.VoucherViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EditVoucherFragment extends Fragment {

    public static final String ARG_VOUCHER_ID = "ARG_VOUCHER_ID";

    private TextInputLayout tilCode, tilValue, tilStart, tilEnd, tilType, tilStatus;
    private TextInputEditText etCode, etValue, etStart, etEnd;
    private MaterialAutoCompleteTextView actType, actStatus;
    private ImageButton btnClose;
    private View btnUpdate, progress;

    private VoucherViewModel viewModel;
    private String voucherId;

    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final DecimalFormat df = new DecimalFormat("#.##");

    public static EditVoucherFragment newInstance(String voucherId) {
        EditVoucherFragment fragment = new EditVoucherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VOUCHER_ID, voucherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ymd.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (getArguments() != null) {
            voucherId = getArguments().getString(ARG_VOUCHER_ID);
        }

        if (TextUtils.isEmpty(voucherId)) {
            Toast.makeText(getContext(), "Missing voucher id", Toast.LENGTH_LONG).show();
            popBackStack();
            return;
        }
        viewModel = new ViewModelProvider(requireActivity()).get(VoucherViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupDropdowns();
        setupActions();
        observeViewModel();

        viewModel.loadVoucherDetail(voucherId);
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

        btnClose = view.findViewById(R.id.btnClose);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        progress = view.findViewById(R.id.progress);
    }

    private void setupDropdowns() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_list_item_1,
            getResources().getStringArray(R.array.voucher_types));
        actType.setAdapter(typeAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_list_item_1,
            getResources().getStringArray(R.array.voucher_statuses));
        actStatus.setAdapter(statusAdapter);
    }

    private void setupActions() {

        etStart.setOnClickListener(v -> pickDate(etStart));
        etEnd.setOnClickListener(v -> pickDate(etEnd));

        btnClose.setOnClickListener(v -> {
            popBackStack();
        });

        btnUpdate.setOnClickListener(v -> {
            clearErrors();
            VoucherRequest req = buildRequest();

            viewModel.patchVoucher(voucherId, req);
        });
    }

    private void pickDate(TextInputEditText target) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Chọn ngày")
            .build();
        picker.show(getParentFragmentManager(), "date");

        picker.addOnPositiveButtonClickListener(sel -> {
            Date d = new Date(sel);
            target.setText(ymd.format(d));
        });
    }

    private void observeViewModel() {
        viewModel.detailState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            switch (state.status) {
                case LOADING:
                    setLoading(true);
                    break;
                case LOADED:
                    setLoading(false);
                    fillForm(state.data);
                    break;
                case ERROR:
                    setLoading(false);
                    Toast.makeText(getContext(), state.message, Toast.LENGTH_LONG).show();
                    popBackStack();
                    break;
                default:
                    setLoading(false);
            }
        });

        viewModel.saveState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            switch (state.status) {
                case SAVING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    viewModel.clearSaveState();
                    popBackStack();
                    break;
                case ERROR:
                    setLoading(false);
                    Toast.makeText(getContext(), state.message, Toast.LENGTH_LONG).show();
                    viewModel.clearSaveState();
                    break;
                default:
                    setLoading(false);
            }
        });
    }

    private void setLoading(boolean b) {
        progress.setVisibility(b ? View.VISIBLE : View.GONE);
        btnUpdate.setEnabled(!b);
        btnClose.setEnabled(!b);
    }

    private void fillForm(VoucherResponse v) {
        if (v == null) return;
        etCode.setText(n(v.code));
        actType.setText(n(v.type), false);

        if (v.value instanceof Number) {
            etValue.setText(df.format(((Number) v.value).doubleValue()));
        } else {
            etValue.setText(n(String.valueOf(v.value)));
        }

        etStart.setText(n(v.startDate));
        etEnd.setText(n(v.endDate));
        actStatus.setText(n(v.status), false);
    }

    private VoucherRequest buildRequest() {
        VoucherRequest r = new VoucherRequest();
        r.code = t(etCode);
        r.type = t(actType);
        r.startDate = t(etStart);
        r.endDate = t(etEnd);
        r.status = t(actStatus);

        String raw = t(etValue).replace(",", "").trim();
        try {
            java.math.BigDecimal bd = new java.math.BigDecimal(raw);
            r.value = bd.doubleValue();
        } catch (Exception e) {
            r.value = Double.NaN;
        }
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

    private static String n(String s) { return s == null ? "" : s; }
    private static String t(TextInputEditText et) { return et.getText() == null ? "" : et.getText().toString().trim(); }
    private static String t(MaterialAutoCompleteTextView v) { return v.getText() == null ? "" : v.getText().toString().trim(); }

    private void popBackStack() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.clearDetailState();
        viewModel.clearSaveState();
    }
}
