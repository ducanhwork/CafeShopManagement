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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.common.util.HttpException;
import com.group3.application.model.bean.LoyaltyMemberListItem;
import com.group3.application.view.adapter.LoyaltyAdapter;
import com.group3.application.viewmodel.LoyaltyViewModel;

public class LoyaltyListFragment extends Fragment implements LoyaltyAdapter.OnLoyaltyMemberClickListener {
    private LoyaltyViewModel viewModel;
    private RecyclerView recyclerView;
    private LoyaltyAdapter adapter;
    private ProgressBar progressBar;
    private TextInputEditText etSearch;
    private Button btnSearch;

    private String currentQuery = "";
    private String currentSortBy = "name";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LoyaltyViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loyalty_list, container, false);

        recyclerView = view.findViewById(R.id.rv_loyalty_members);
        progressBar = view.findViewById(R.id.progress_bar);
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);

        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSearch.setOnClickListener(v -> {
            currentQuery = etSearch.getText().toString().trim();
            currentSortBy = "name";
            viewModel.loadMembers(currentQuery, currentSortBy);
        });

        observeViewModel();

        viewModel.loadMembers(currentQuery, currentSortBy);
    }

    private void setupRecyclerView() {
        adapter = new LoyaltyAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.memberList.observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.isSuccess()) {
                adapter.submitList(result.data);
            } else if (result != null) {
                String errorMessage = result.error.getMessage();
                if (result.error instanceof HttpException) {
                    HttpException httpError = (HttpException) result.error;
                    errorMessage = "Lỗi " + httpError.code;
                }
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.updateResult.observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.isSuccess()) {
                Toast.makeText(getContext(), "Đang làm mới danh sách...", Toast.LENGTH_SHORT).show();
                viewModel.loadMembers(currentQuery, currentSortBy);
                viewModel.clearUpdateResult();
            }
        });
    }

    @Override
    public void onEditClick(LoyaltyMemberListItem member) {
        EditMemberFragment editFragment = EditMemberFragment.newInstance(
            member.getId(),
            member.getFullName(),
            member.getPhone(),
            member.getEmail()
        );

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit();
        }
    }

    @Override
    public void onItemClick(LoyaltyMemberListItem member) {
        PointsHistoryFragment historyFragment = PointsHistoryFragment.newInstance(
            member.getId(),
            member.getFullName()
        );

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, historyFragment)
                .addToBackStack(null)
                .commit();
        }
    }
}
