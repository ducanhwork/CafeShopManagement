package com.group3.application.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.common.util.HttpException;
import com.group3.application.view.adapter.PointsHistoryAdapter;
import com.group3.application.viewmodel.LoyaltyViewModel;

import java.util.UUID;

public class PointsHistoryFragment extends Fragment {

    private static final String ARG_CUSTOMER_ID = "CUSTOMER_ID";
    private static final String ARG_CUSTOMER_NAME = "CUSTOMER_NAME";

    private LoyaltyViewModel viewModel;
    private PointsHistoryAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private UUID customerId;
    private String customerName;

    public static PointsHistoryFragment newInstance(UUID customerId, String customerName) {
        PointsHistoryFragment fragment = new PointsHistoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CUSTOMER_ID, customerId);
        args.putString(ARG_CUSTOMER_NAME, customerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = (UUID) getArguments().getSerializable(ARG_CUSTOMER_ID);
            customerName = getArguments().getString(ARG_CUSTOMER_NAME);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(LoyaltyViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_points_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof AppCompatActivity && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Lịch sử điểm: " + customerName);
        }

        recyclerView = view.findViewById(R.id.rv_history);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);

        setupRecyclerView();
        observeViewModel();

        if (customerId != null) {
            viewModel.loadPointsHistory(customerId);
        }
    }

    private void setupRecyclerView() {
        adapter = new PointsHistoryAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.historyList.observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                if (result.data == null || result.data.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.submitList(result.data);
                }
            } else {
                String error = (result.error != null) ? result.error.getMessage() : "Lỗi không xác định";
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof AppCompatActivity && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Khách hàng thân thiết");
        }
    }
}
