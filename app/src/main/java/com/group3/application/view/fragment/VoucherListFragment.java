package com.group3.application.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.model.bean.VoucherResponse;
import com.group3.application.view.adapter.VoucherAdapter;
import com.group3.application.viewmodel.VoucherViewModel;


public class VoucherListFragment extends Fragment implements VoucherAdapter.OnVoucherClickListener {

    private VoucherViewModel viewModel;
    private RecyclerView rvVouchers;
    private VoucherAdapter adapter;
    private TextView tvState;

    private TextInputEditText etCode;
    private MaterialAutoCompleteTextView actType, actStatus, actSortBy;
    private View btnApply;
    private FloatingActionButton fabAddVoucher;

    private View headerFilter, layoutExpandable, btnClear;
    private ImageView ivExpand;

    private String currentCode = "", currentStatus = "", currentType = "";
    private String currentSortBy = "startDate";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(VoucherViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voucher_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupFilterExpansion();
        setupFilters();
        setupRecycler();
        setupClickListeners();
        observeViewModel();

        viewModel.loadList(currentCode, currentStatus, currentType, currentSortBy);
    }

    private void bindViews(View view) {
        headerFilter = view.findViewById(R.id.headerFilter);
        layoutExpandable = view.findViewById(R.id.layoutExpandable);
        ivExpand = view.findViewById(R.id.ivExpand);
        btnClear = view.findViewById(R.id.btnClear);
        rvVouchers = view.findViewById(R.id.rvVouchers);
        tvState = view.findViewById(R.id.tvState);

        etCode = view.findViewById(R.id.etCode);
        actType = view.findViewById(R.id.actType);
        actStatus = view.findViewById(R.id.actStatus);
        actSortBy = view.findViewById(R.id.actSortBy);
        btnApply = view.findViewById(R.id.btnApply);

        fabAddVoucher = view.findViewById(R.id.fabAddVoucher);
    }

    private void setupRecycler() {
        adapter = new VoucherAdapter(this);
        rvVouchers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVouchers.setAdapter(adapter);
    }

    private void setupFilters() {
        actType.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
            new String[]{"", "PERCENT", "FIXED_AMOUNT"}));
        actStatus.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
            new String[]{"", "ACTIVE", "INACTIVE"}));
        actSortBy.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
            new String[]{"startDate", "endDate"}));
        actSortBy.setText("startDate", false);

        btnApply.setOnClickListener(v -> {
            currentCode = txt(etCode);
            currentStatus = txt(actStatus);
            currentType = txt(actType);
            currentSortBy = txt(actSortBy);
            viewModel.loadList(currentCode, currentStatus, currentType, currentSortBy);
        });
    }


    private void setupClickListeners() {
        fabAddVoucher.setOnClickListener(v -> {
            navigateTo(new CreateVoucherFragment());
        });
    }

    private void observeViewModel() {
        viewModel.listState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            switch (state.status) {
                case LOADING:
                    showState(true, "Đang tải...");
                    rvVouchers.setVisibility(View.GONE);
                    break;
                case LOADED:
                    if (state.data == null || state.data.isEmpty()) {
                        showState(false, "Không tìm thấy voucher nào");
                        rvVouchers.setVisibility(View.GONE);
                    } else {
                        hideState();
                        rvVouchers.setVisibility(View.VISIBLE);
                        adapter.submitList(state.data);
                    }
                    break;
                case ERROR:
                    showState(false, state.message);
                    rvVouchers.setVisibility(View.GONE);
                    break;
                default:
                    hideState();
            }
        });

        viewModel.saveState.observe(getViewLifecycleOwner(), state -> {
            if (state != null && state.status == VoucherViewModel.ViewState.Status.SUCCESS) {
                Toast.makeText(getContext(), "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                viewModel.loadList(currentCode, currentStatus, currentType, currentSortBy);
                viewModel.clearSaveState();
            }
        });
    }

    @Override
    public void onVoucherClick(VoucherResponse voucher) {
        if (voucher.id != null) {
            EditVoucherFragment editFragment = EditVoucherFragment.newInstance(voucher.id.toString());
            navigateTo(editFragment);
        }
    }

    private void navigateTo(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_voucher, fragment)
                .addToBackStack(null)
                .commit();
        }
    }

    private void setupFilterExpansion() {
        headerFilter.setOnClickListener(v -> {
            boolean isVisible = layoutExpandable.getVisibility() == View.VISIBLE;
            layoutExpandable.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            ivExpand.setRotation(isVisible ? 0 : 180);
        });
        btnClear.setOnClickListener(v -> {
            etCode.setText("");
            actType.setText("", false);
            actStatus.setText("", false);
            actSortBy.setText("startDate", false);
        });
    }

    private String txt(TextView v) {
        return v.getText() == null ? "" : v.getText().toString().trim();
    }

    private void showState(boolean loading, String msg) {
        tvState.setVisibility(View.VISIBLE);
        tvState.setText(loading ? "Đang tải..." : msg);
    }

    private void hideState() {
        tvState.setVisibility(View.GONE);
    }

}
