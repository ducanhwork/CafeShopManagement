package com.group3.application.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.group3.application.R;
import com.group3.application.common.util.JWTUtils;
import com.group3.application.common.util.PreferenceManager;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.adapter.TableManagementAdapter;
import com.group3.application.viewmodel.TableManagementViewModel;

/**
 * Table Management Fragment for Manager role
 * Displays table grid with Material Design 3
 * Features: Grid view, filters, search, FAB for add table, CRUD operations
 */
public class TableManagementFragment extends Fragment {

    private TableManagementViewModel viewModel;
    private TableManagementAdapter adapter;
    
    // Views
    private SearchBar searchBar;
    private ChipGroup chipGroupStatus;
    private ChipGroup chipGroupLocation;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddTable;
    
    // Filter states
    private String currentStatusFilter = null;
    private String currentLocationFilter = null;
    private String currentSearchQuery = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_management, container, false);
        
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupFilters();
        setupSearchBar();
        setupFAB();
        
        // Initial load
        loadTables();
        
        return view;
    }

    private void initViews(View view) {
        searchBar = view.findViewById(R.id.searchBar);
        chipGroupStatus = view.findViewById(R.id.chipGroupStatus);
        chipGroupLocation = view.findViewById(R.id.chipGroupLocation);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerViewTables);
        fabAddTable = view.findViewById(R.id.fabAddTable);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(TableManagementViewModel.class);
        
        // Observe tables
        viewModel.getTableList().observe(getViewLifecycleOwner(), tables -> {
            if (tables != null) {
                adapter.setData(tables);
            }
        });
        
        // Observe loading state
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(Boolean.TRUE.equals(isLoading));
        });
        
        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new TableManagementAdapter(new TableManagementAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TableInfo table) {
                // Show table details or edit
                showTableActionsBottomSheet(table);
            }

            @Override
            public void onItemLongClick(TableInfo table) {
                // Show actions bottom sheet
                showTableActionsBottomSheet(table);
            }
        });
        
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // Swipe to refresh
        swipeRefresh.setOnRefreshListener(this::loadTables);
    }

    private void setupFilters() {
        // Status filter
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentStatusFilter = null;
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipAll) {
                    currentStatusFilter = null;
                } else if (checkedId == R.id.chipAvailable) {
                    currentStatusFilter = "Available";
                } else if (checkedId == R.id.chipOccupied) {
                    currentStatusFilter = "Occupied";
                } else if (checkedId == R.id.chipReserved) {
                    currentStatusFilter = "Reserved";
                }
            }
            loadTables();
        });
        
        // Location filter
        chipGroupLocation.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentLocationFilter = null;
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipAllLocation) {
                    currentLocationFilter = null;
                } else if (checkedId == R.id.chipIndoor) {
                    currentLocationFilter = "Indoor";
                } else if (checkedId == R.id.chipOutdoor) {
                    currentLocationFilter = "Outdoor";
                } else if (checkedId == R.id.chipBalcony) {
                    currentLocationFilter = "Balcony";
                }
            }
            loadTables();
        });
    }

    private void setupSearchBar() {
        searchBar.addTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim();
                if (currentSearchQuery.isEmpty()) {
                    currentSearchQuery = null;
                }
                loadTables();
            }
        });
    }

    private void setupFAB() {
        // Check user role and show/hide FAB accordingly
        String token = PreferenceManager.getToken(requireContext());
        if (token != null) {
            String role = JWTUtils.getRoleFromToken(token);
            // Only show FAB for Manager
            if ("MANAGER".equals(role)) {
                fabAddTable.setVisibility(View.VISIBLE);
            } else {
                fabAddTable.setVisibility(View.GONE);
            }
        }
        
        fabAddTable.setOnClickListener(v -> {
            // TODO: Show AddEditTableBottomSheet
            Toast.makeText(requireContext(), 
                "Add table feature coming soon", 
                Toast.LENGTH_SHORT).show();
        });
        
        // Hide FAB on scroll down, show on scroll up
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAddTable.hide();
                } else if (dy < 0) {
                    fabAddTable.show();
                }
            }
        });
    }

    private void loadTables() {
        // Load tables with current filters
        viewModel.loadTables(currentStatusFilter, currentLocationFilter);
    }
    
    private void showTableActionsBottomSheet(TableInfo table) {
        // TODO: Implement TableActionsBottomSheet
        Toast.makeText(requireContext(), 
            "Actions for: " + table.getName(), 
            Toast.LENGTH_SHORT).show();
    }
}
