package com.group3.application.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.OrderItemDTO;
import com.group3.application.view.OrderListActivity;
import com.group3.application.view.adapter.OrderSummaryAdapter;
import com.group3.application.viewmodel.OrderViewModel;

import java.util.Locale;
import java.util.Objects;

public class OrderSummaryFragment extends Fragment {

    private OrderViewModel orderVM;
    private OrderSummaryAdapter adapter;
    private TextView tvSubtotal, tvDiscount, tvTotal, tvHeader, tvStaffName;
    private TextInputEditText etNote;

    public OrderSummaryFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderVM = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        MaterialToolbar tb = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(tb);
        tb.setTitle("Summary - " + (orderVM.getTableNames() == null ? "" : orderVM.getTableNames()));
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        RecyclerView rv = view.findViewById(R.id.rvSummary);
        tvHeader = view.findViewById(R.id.tvHeader);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvTotal = view.findViewById(R.id.tvTotal);
        MaterialButton btnConfirm = view.findViewById(R.id.btnConfirm);
        tvStaffName = view.findViewById(R.id.tv_staff_name);
        etNote = view.findViewById(R.id.et_note);

        tvHeader.setText("Order summary for table: " + orderVM.getTableNames());

        adapter = new OrderSummaryAdapter(new OrderSummaryAdapter.OnQuantityChangedListener() {
            @Override
            public void onIncrease(OrderItemDTO item) {
                orderVM.updateQuantityForSummary(item, item.quantity + 1);
            }

            @Override
            public void onDecrease(OrderItemDTO item) {
                orderVM.updateQuantityForSummary(item, item.quantity - 1);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        observeViewModel();

        btnConfirm.setOnClickListener(v -> {
            String note = etNote.getText() != null ? etNote.getText().toString().trim() : "";
            orderVM.setNote(note);
            orderVM.submitOrder();
        });
    }

    private void observeViewModel(){
        orderVM.getCurrentOrderItems().observe(getViewLifecycleOwner(), items -> {
            adapter.submit(items);
            if (items == null || items.isEmpty()) {
                if (isAdded()) {

                }
            }
        });

        orderVM.getTotalAmount().observe(getViewLifecycleOwner(), total -> {
            tvSubtotal.setText(fmt(total) + " đ");
            tvDiscount.setText(fmt(0d) + " đ");
            tvTotal.setText(fmt(total) + " đ");
        });

        orderVM.currentUser.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvStaffName.setText(user.getFullname());
            }
        });

        orderVM.orderSubmissionResult.observe(getViewLifecycleOwner(), event -> {
            APIResult result = event.getContentIfNotHandled();
            if (result == null) return;

            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Đặt món thành công!", Toast.LENGTH_LONG).show();
                if (isAdded()) {
                    Intent intent = new Intent(requireActivity(), OrderListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                }
            } else {
                Toast.makeText(requireContext(), "Lỗi: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String fmt(double d) {
        if (Math.abs(d - Math.round(d)) < 1e-6) return String.valueOf((long) Math.round(d));
        return String.format(Locale.getDefault(), "%,.0f", d);
    }
}
